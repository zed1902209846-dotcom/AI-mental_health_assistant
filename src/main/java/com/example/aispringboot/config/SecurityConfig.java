package com.example.aispringboot.config;

import cn.hutool.core.text.AntPathMatcher;
import com.example.aispringboot.util.JwtAuthticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/api/text",
            "/api/user/login",
            "/api/user/add"
    };

    public static Boolean isPublicPATH(String requesturl) {
        for (String publicPath : PUBLIC_PATHS) {
            if (antPathMatcher.match(publicPath, requesturl)) {
                return true;
            }
        }
        return false;
    }
    @Bean
    public JwtAuthticationFilter jwtAuthticationFilter() {
        return new JwtAuthticationFilter();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //禁用 csrf 保护(API服务通常不需要csrf保护)
                .csrf(AbstractHttpConfigurer::disable)
                //配置会话管理为无状态(JWT需要;无状态会话管理是指在请求之间不维护会话状态，每个请求都是独立的)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //配置请求的授权规则
                .authorizeHttpRequests(auth ->auth
                        //公开的路径(无需登录即可访问)
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        //其他路径都需要登录
                        .anyRequest().authenticated()
                )
                //添加 JWT认证过滤器
                .addFilterBefore(jwtAuthticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

}
