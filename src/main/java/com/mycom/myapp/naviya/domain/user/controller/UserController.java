package com.mycom.myapp.naviya.domain.user.controller;

import com.mycom.myapp.naviya.domain.user.dto.SignupRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupResultDto;
import com.mycom.myapp.naviya.domain.user.dto.UserDto;
import com.mycom.myapp.naviya.domain.user.dto.UserResultDto;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


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


    // 유저페이지 정보 조회 (로그인한 사용자)
    @GetMapping("/userpage")
    public String detailUserPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // 현재 로그인한 사용자의 이메일을 가져옴

        if (authentication != null) {
            LOGGER.info("User email: {}", authentication.getName());
            LOGGER.info("Authorities: {}", authentication.getAuthorities());
        } else {
            LOGGER.error("No authenticated user found.");
        }

        User user = userRepository.findByEmail(email);
        UserResultDto userResultDto =  userService.detailUserPage(email);
        // 사용자 정보를 Model에 추가하여 View로 전달
        model.addAttribute("userDto", userResultDto.getUserDto());
        model.addAttribute("user", user);

        return "userpage";
    }


    @PostMapping("/userpage/update")
    @ResponseBody
    public UserResultDto updateUserPage(@RequestBody UserDto userDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // 현재 로그인한 사용자의 이메일을 가져옴

        User user = userRepository.findByEmail(email);
        userDto.setUserId(user.getUserId());

        // 사용자 정보 수정
        return userService.updateUserPage(userDto);
    }


}
