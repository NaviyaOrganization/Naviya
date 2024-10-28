package com.mycom.myapp.naviya.global.security.config;

import com.mycom.myapp.naviya.global.security.dto.CustomUserDetails;
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

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        System.out.println("----------------------------------");
        System.out.println(userDetails);
        System.out.println("----------------------------------");

        // 로그인 성공 시 사용자 정보를 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("userEmail", userDetails.getUsername());


        // 기본 로그인 성공 후의 페이지로 리다이렉트
        response.sendRedirect("/children/select");
    }
}