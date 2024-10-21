package com.mycom.myapp.naviya.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().permitAll()  // 모든 요청을 인증 없이 허용
                )
                .csrf((csrf) -> csrf.disable());  // CSRF 보호 비활성화

        return http.build();
    }
}

