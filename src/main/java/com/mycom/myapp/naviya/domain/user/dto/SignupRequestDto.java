package com.mycom.myapp.naviya.domain.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignupRequestDto {
    private String email;
    private String password;
    private String name;
    private String phone;
}
