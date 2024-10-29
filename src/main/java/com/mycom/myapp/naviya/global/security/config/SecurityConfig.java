package com.mycom.myapp.naviya.global.security.config;

import com.mycom.myapp.naviya.domain.common.repository.AdminRepository;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.global.security.service.CustomAdminDetailService;
import com.mycom.myapp.naviya.global.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(new CustomUserDetailService(userRepository));
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(new CustomAdminDetailService(adminRepository));
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/admin/**")) // 관리자 경로에 대한 보안 설정
                .authenticationProvider(adminAuthenticationProvider()) // 관리자 인증 프로바이더 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/admin/signup", "/admin/login", "/admin/loginProc").permitAll() // 로그인과 회원가입 경로 허용 // 관리자 가입은 추후에 막을 예정
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 접근 제한
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/admin/login") // 관리자 로그인 페이지
                        .loginProcessingUrl("/admin/loginProc") // 관리자 로그인 처리 경로
                        .usernameParameter("adminEmail")
                        .passwordParameter("adminPassword")
                        .successHandler(new CustomAdminLoginSuccessHandler())
                        .failureUrl("/admin/login?error") // 실패 시 이동할 경로
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/admin/**"))) // 관리자가 아닌 모든 경로
                .authenticationProvider(userAuthenticationProvider()) // 사용자 인증 프로바이더 설정
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/", "/login", "/signup", "/signupProc",
                                "/static/**","/assets/img/**", "/css/**", "/js/**", "/images/**", "/Book/**", "templates/BookCategoryHtml.html/**").permitAll() // 사용자 접근 경로 허용
                        .anyRequest().authenticated() // 모든 다른 요청은 인증 요구
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // 사용자 로그인 페이지
                        .loginProcessingUrl("/loginProc") // 사용자 로그인 처리 경로
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(new CustomLoginSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        http.sessionManagement(auth -> auth
                .maximumSessions(5)
                .maxSessionsPreventsLogin(true));

        return http.build();
    }
}