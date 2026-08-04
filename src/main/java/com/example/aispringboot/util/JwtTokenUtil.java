package com.example.aispringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.aispringboot.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
public class JwtTokenUtil implements ApplicationContextAware  {
    private static final String ISSUER = "mental-health-assistant";

    private static ApplicationContext applicationContext;
    //用于在静态工具类中获取 Spring 容器管理的 Bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtil.applicationContext = applicationContext;
    }

    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }


    //生成token的方法
    public static String generateToken(Long userId, String username, Integer roleType) {
        try{
            // 获取 jwt 的配置
            JwtConfig jwtConfig = getJwtConfig();
            //生成签名的算法
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            //生成过期时间
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());

            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expiration)//设置过期时间
                    .withIssuedAt(new Date())//设置签发时间
                    .withIssuer(ISSUER)//设置签发者
                    .sign(algorithm);//签名

            return token;
        }catch (Exception e){
            throw new RuntimeException("生成token失败:" + e);
        }
    }
    //提取token
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if (request==null){
            return null;
        }

        String tokenHeader = request.getHeader("token");
        if (StringUtils.hasText(tokenHeader)){
            return tokenHeader;
        }
        return null;
    }
    //验证token
    public static TokenValidationResult validateToken(String token){
        //ai 新增(有了这段代码,无效 token 会转成 return null,让
        // validateToken 的调用方(过滤器)能正常判断并返回 401,
        // 把"token 无效"当作业务上的正常分支处理,而不是系统崩溃)
        DecodedJWT jwt;
        try {
            jwt = verifyToken(token);
        } catch (JWTVerificationException e) {
            return null;
        }
        //
        Long userId = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();
        //角色类型有点特殊
        Integer roleType = null ;
        try{
            roleType = jwt.getClaim("roleType").asInt();
        }catch (Exception e){
            String roleTypeStr = jwt.getClaim("roleType").asString();
            if (StringUtils.hasText(roleTypeStr)){
                roleType = Integer.valueOf(roleTypeStr);

            }
        }
        if (userId != null && StringUtils.hasText(username) && roleType != null){
            return new TokenValidationResult(userId, username, roleType, true);
        }
        return null;
    }
    //验证token的有效性
    public static DecodedJWT verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("token不能为空");
        }
        //token解码
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        return verifier.verify(token);
    }

    //token验证结果返回类
    @Getter
    public static class TokenValidationResult {
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;

        public TokenValidationResult(Long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }
}
