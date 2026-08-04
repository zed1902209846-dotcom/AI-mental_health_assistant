package com.example.aispringboot.util;

import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
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
