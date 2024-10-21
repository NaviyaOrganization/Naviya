package com.mycom.myapp.naviya.domain.user.service;

import com.mycom.myapp.naviya.domain.user.dto.LoginRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.LoginResultDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupResultDto;

public interface UserService {

    public SignupResultDto signup(SignupRequestDto signupRequestDto);
    public LoginResultDto login(LoginRequestDto loginRequestDto);
}
