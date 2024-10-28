package com.mycom.myapp.naviya.domain.common.service;

import com.mycom.myapp.naviya.domain.common.dto.AdminSignupRequestDto;
import com.mycom.myapp.naviya.domain.common.dto.AdminSignupResultDto;

public interface AdminService {
    public Long getAdminId(String email);
    public AdminSignupResultDto signup(AdminSignupRequestDto adminSignupRequestDto);
}
