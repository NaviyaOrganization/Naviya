package com.mycom.myapp.naviya.global.controller;

import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginP() {

        return "login";
    }

    @GetMapping("/signup")
    public String signupP() {

        return "signup";
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자인지 확인
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();  // 현재 로그인한 사용자의 이메일을 가져옴
            User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);  // 비로그인 상태일 경우 user에 null 설정
        }

        return "index"; // templates 안 index.html 렌더링
    }

}
