package com.mycom.myapp.naviya.domain.user.controller;

import com.mycom.myapp.naviya.domain.user.dto.SignupRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupResultDto;
import com.mycom.myapp.naviya.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/naviya")
@ResponseBody
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signupProc")
    public String signup(SignupRequestDto signupRequestDto) {
        System.out.println(signupRequestDto.getEmail());

        userService.signup(signupRequestDto);

        return "redirect:/login" ;
    }

}
