package com.mycom.myapp.naviya.domain.common.service;

import com.mycom.myapp.naviya.domain.common.dto.AdminSignupRequestDto;
import com.mycom.myapp.naviya.domain.common.dto.AdminSignupResultDto;
import com.mycom.myapp.naviya.domain.common.entity.Admin;
import com.mycom.myapp.naviya.domain.common.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public Long getAdminId(String adminEmail) {
        Admin admin = adminRepository.findByAdminEmail(adminEmail);
        return admin.getAdminId();
    }

    // 관리자 가입
    @Override
    public AdminSignupResultDto signup(AdminSignupRequestDto adminSignupRequestDto) {
        AdminSignupResultDto adminSignupResultDto = new AdminSignupResultDto();

        String adminEmail = adminSignupRequestDto.getAdminEmail();
        String adminPassword = adminSignupRequestDto.getAdminPassword();
        String adminName = adminSignupRequestDto.getAdminName();

        // 이메일 중복 확인
        Boolean isExist = adminRepository.existsByAdminEmail(adminEmail);
        if(isExist) {
            LOGGER.info("[SIGNUP] 이메일 중복");
            adminSignupResultDto.setStatus("이메일 중복입니다.");
            return adminSignupResultDto;
        }

        // 회원가입 진행
        Admin admin = new Admin();
        admin.setAdminEmail(adminEmail);
        admin.setAdminPassword(bCryptPasswordEncoder.encode(adminPassword));
        admin.setAdminName(adminName);
        admin.setCreatedAt(LocalDateTime.now()); // 회원가입 시각 자동 저장
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRole("ROLE_ADMIN");

        Admin savedAdmin = adminRepository.save(admin);

        if (!savedAdmin.getAdminEmail().isEmpty()) {
            LOGGER.info("[SIGNUP] 관리자 가입 성공");
            adminSignupResultDto.setStatus("관리자 가입 성공");
        } else {
            LOGGER.info("[SIGNUP] 관리자 가입 실패");
            adminSignupResultDto.setStatus("관리자 가입 실패");
        }

        return adminSignupResultDto;
    }
}
