package com.example.aispringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.aispringboot.config.JwtConfig;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
}
