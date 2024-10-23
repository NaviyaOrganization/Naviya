package com.mycom.myapp.naviya.global.controller;

import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {

        return "signup";
    }

    @GetMapping("/")
    public String mainPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일, 자녀 아이디를 가져옴
        String email = (String) session.getAttribute("userEmail");
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");

        System.out.println("---------------------------");
        System.out.println(selectedChildId);
        System.out.println("---------------------------");

        if (email != null) {  // 세션에 저장된 이메일이 있을 경우
            User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);  // 세션에 값이 없으면 비로그인 상태로 처리
        }

        return "index"; // templates 안 index.html 렌더링
    }

    @GetMapping("/children/addPage")
    public String childAddPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
        model.addAttribute("user", user);

        return "childAdd"; // templates 안 childAdd.html 렌더링
    }

}
