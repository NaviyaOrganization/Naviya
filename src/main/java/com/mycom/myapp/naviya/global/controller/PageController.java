package com.mycom.myapp.naviya.global.controller;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;

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

        if (selectedChildId != null) { // 세션에 저장된 자녀 ID가 있을 경우
            Child child = childRepository.findByChildId(selectedChildId);
            model.addAttribute("child", child);
        } else {
            model.addAttribute("child", null);
        }

        if (email != null) {  // 세션에 저장된 이메일이 있을 경우
            User user = userRepository.findByEmail(email);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }

        return "index";
    }

    @GetMapping("/children/addPage")
    public String childAddPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "childAdd";
    }


    @GetMapping("/ChildFavorBookList")
    public String ChildFavorBookListChildFavorBookList(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "BookCategoryHtml";
    }
    @GetMapping("/ChildRecentReadBook")
    public String   ChildRecentReadBook(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "ChildRecentReadBook";
    }
    @GetMapping("/BookCategoryHtml")
    public String   BookCategory(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "BookCategoryHtml";
    }

    @GetMapping("/search")
    public String search(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "search";
    }
}

