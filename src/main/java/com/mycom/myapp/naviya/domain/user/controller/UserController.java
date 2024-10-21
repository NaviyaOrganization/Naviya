package com.mycom.myapp.naviya.domain.user.controller;

import com.mycom.myapp.naviya.domain.user.dto.SignupRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupResultDto;
import com.mycom.myapp.naviya.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

@RestController
@RequestMapping
@ResponseBody
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


//    // 회원가입
//    @PostMapping("/signupProc")
//    public String signup(SignupRequestDto signupRequestDto) {
//        System.out.println(signupRequestDto.getEmail());
//
//        userService.signup(signupRequestDto);
//
//        return "login" ;
//    }

    // 회원가입 처리
    @PostMapping("/signupProc")
    public ResponseEntity<SignupResultDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupResultDto signupResultDto = userService.signup(signupRequestDto);

        // 상태와 메시지에 따라 다른 응답 반환
        if ("이메일 중복입니다.".equals(signupResultDto.getStatus())) {
            return ResponseEntity.badRequest().body(signupResultDto); // 400 Bad Request
        } else if ("회원가입 성공".equals(signupResultDto.getStatus())) {
            return ResponseEntity.ok(signupResultDto); // 200 OK
        } else {
            return ResponseEntity.status(500).body(signupResultDto); // 500 Internal Server Error for unexpected case
        }
    }

    // 메인 페이지
    @GetMapping("/")
    public String main(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println("---------------------------------------");
        System.out.println(email);
        System.out.println("---------------------------------------");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream().findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_USER");

        model.addAttribute("email", email);
        model.addAttribute("role", role);

        return "main";
    }


}
