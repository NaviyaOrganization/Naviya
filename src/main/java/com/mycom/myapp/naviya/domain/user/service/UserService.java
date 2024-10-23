package com.mycom.myapp.naviya.domain.user.service;

import com.mycom.myapp.naviya.domain.user.dto.SignupRequestDto;
import com.mycom.myapp.naviya.domain.user.dto.SignupResultDto;
import com.mycom.myapp.naviya.domain.user.dto.UserDto;
import com.mycom.myapp.naviya.domain.user.dto.UserResultDto;

public interface UserService {

    public SignupResultDto signup(SignupRequestDto signupRequestDto);
    public UserResultDto detailUserPage(String email);
    public UserResultDto updateUserPage(UserDto userDto);
}
