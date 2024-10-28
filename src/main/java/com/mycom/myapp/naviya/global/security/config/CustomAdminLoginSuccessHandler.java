package com.mycom.myapp.naviya.global.security.config;

import com.mycom.myapp.naviya.global.security.dto.CustomAdminDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class CustomAdminLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = Logger.getLogger(CustomAdminLoginSuccessHandler.class.getName());
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 인증된 사용자 정보
        CustomAdminDetails adminDetails = (CustomAdminDetails) authentication.getPrincipal();
        String email = adminDetails.getUsername(); // 이메일 확인

        // 로그 확인
        logger.config("Admin logged in with email: " + email);
        System.out.println("Admin logged in with email: " + email);

        // 로그인 성공 시 사용자 정보를 세션에 저장
        HttpSession session = request.getSession();
        if (session == null) {
            // 세션이 null인 경우 처리
            System.out.println("Session is null. Cannot store email.");
            return;
        }
        session.setAttribute("adminEmail", email);

        // 세션에 저장된 이메일 확인
        String storedEmail = (String) session.getAttribute("adminEmail");
        System.out.println("Stored email in session: " + storedEmail);

        // 기본 로그인 성공 후의 페이지로 리다이렉트
        response.sendRedirect("/admin");
    }
}