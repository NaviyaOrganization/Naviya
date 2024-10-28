package com.mycom.myapp.naviya.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminSignupRequestDto {

    private String adminEmail;
    private String adminPassword;
    private String adminName;
}
