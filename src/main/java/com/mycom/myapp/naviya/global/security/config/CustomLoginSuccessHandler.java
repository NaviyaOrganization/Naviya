package com.mycom.myapp.naviya.global.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 로그인 성공 시 사용자 정보를 세션에 저장
        HttpSession session = request.getSession();
        String email = authentication.getName();
        session.setAttribute("userEmail", email);


        // 기본 로그인 성공 후의 페이지로 리다이렉트
        response.sendRedirect("/children/select");
    }
}