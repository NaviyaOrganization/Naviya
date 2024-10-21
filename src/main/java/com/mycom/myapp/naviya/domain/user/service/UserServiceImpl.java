package com.mycom.myapp.naviya.domain.user.service;

import com.mycom.myapp.naviya.domain.user.dto.LoginRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.LoginResultDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupResultDto;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.global.config.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;



    /*
    회원가입
    */
    @Override
    public SignupResultDto signup(SignupRequestDto signupRequestDto) {
        SignupResultDto signupResultDto = new SignupResultDto();

        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        String name = signupRequestDto.getName();
        String phone = signupRequestDto.getPhone();

        // 이메일 중복 확인
        Boolean isExist = userRepository.existsByEmail(email);
        if(isExist) {
            LOGGER.info("[SIGNUP] 이메일 중복");
            signupResultDto.setStatus("이메일 중복입니다.");
            return signupResultDto;
        }

        // 회원가입 진행
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setName(name);
        user.setPhone(phone);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now())); // 회원가입 시각 자동 저장
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

       User savedUser = userRepository.save(user);

        if (!savedUser.getEmail().isEmpty()) {
                LOGGER.info("[SIGNUP] 회원가입 성공");
                signupResultDto.setStatus("회원가입 성공");
//                setSuccessResult(signupResultDto);
        } else {
                LOGGER.info("[SIGNUP] 회원가입 실패");
                signupResultDto.setStatus("회원가입 실패");
//                setFailResult(signupResultDto);
        }

        return signupResultDto;
    }


    /*
   로그인
   */
    @Override
    public LoginResultDto login(LoginRequestDto loginRequestDto) {
        return null;
    }








    private void setSuccessResult(SignupResultDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    private void setFailResult(SignupResultDto result) {
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }
}
