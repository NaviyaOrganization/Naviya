package com.mycom.myapp.naviya.global.security.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/signupProc", "/children/**",
                                        "/static/**", "/css/**", "/js/**", "/images/**" , "/Book/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(auth -> auth
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .usernameParameter("email")
                        .passwordParameter("password")
//                        .defaultSuccessUrl("/", true)
                        .successHandler(new CustomLoginSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .csrf(csrf -> csrf.disable());

        http
                // 다중 로그인
                .sessionManagement((auth) -> auth
                        .maximumSessions(5)
                        .maxSessionsPreventsLogin(true)); // 초과시 새로운 로그인 차단


        return http.build();
    }
}