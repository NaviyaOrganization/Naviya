package com.mycom.myapp.naviya.domain.common.controller;

import com.mycom.myapp.naviya.domain.common.dto.AdminSignupRequestDto;
import com.mycom.myapp.naviya.domain.common.dto.AdminSignupResultDto;
import com.mycom.myapp.naviya.domain.common.repository.AdminRepository;
import com.mycom.myapp.naviya.domain.common.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);


    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<AdminSignupResultDto> signup(@RequestBody AdminSignupRequestDto adminSignupRequestDto) {
        AdminSignupResultDto adminSignupResultDto = adminService.signup(adminSignupRequestDto);

        // 상태와 메시지에 따라 다른 응답 반환
        if ("이메일 중복입니다.".equals(adminSignupResultDto.getStatus())) {
            return ResponseEntity.badRequest().body(adminSignupResultDto); // 400 Bad Request
        } else if ("관리자 가입 성공".equals(adminSignupResultDto.getStatus())) {
            return ResponseEntity.ok(adminSignupResultDto); // 200 OK
        } else {
            return ResponseEntity.status(500).body(adminSignupResultDto); // 500 Internal Server Error for unexpected case
        }
    }


}
