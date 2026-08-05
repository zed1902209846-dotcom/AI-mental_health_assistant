package com.example.aispringboot.util;

import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.config.SecurityConfig;
import com.example.aispringboot.enumClass.UserStatus;
import com.example.aispringboot.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //注入UserService
    @Resource
    private UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String requestUrl = request.getRequestURI();
        //检查是否为公开路径
        return SecurityConfig.isPublicPATH(requestUrl);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException{
        //获取请求的 URL 和方法
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("requestUrl: " + requestUrl);
        System.out.println("method: " + method);
        //1.提取JWT token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            //2.验证token并获取用户信息
            JwtTokenUtil.TokenValidationResult validationResult = JwtTokenUtil.validateToken(token);
            if (validationResult != null && validationResult.isValid()) {
                //3.查询用户信息验证用户状态
                UserLoginResponseDTO.UserDetailResponseDTO user ;
                try {
                    user = userService.getUserById(validationResult.getUserId());
                } catch (Exception e) {
                    //token 有效但用户已不存在(如账号被删)——视为 token 不可用,按 401 处理
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                    return;
                }
                if (user !=null && UserStatus.NORMAL.getCode().equals(user.getStatus())){
                    //用户状态正常，继续处理请求
                    //4.设置用户信息到SecurityContextHolder(创建 Spring Security认证对象)
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_USER" + validationResult.getRoleType())
                    );

                    //5.创建UsernamePasswordAuthenticationToken对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(),//用户名作为凭证(主体)
                            null,
                            authorities
                    );
                    //6.设置认证信息到Spring Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //将 token 存储到请求属性中
                    request.setAttribute("jwtToken", token);
                }else{
                    //用户状态异常，返回错误响应
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                    return;
                }
            }else{
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
                return;
            }

        }else  {
            //清理上下文
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        //继续过滤器链
        chain.doFilter(request, response);
    }
    //清理Spring security上下文的方法
    private void clearSecurityContext(){
        SecurityContextHolder.clearContext();
    }
}
