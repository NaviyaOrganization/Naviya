package com.mycom.myapp.naviya.domain.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LoginRequestDto {
    private String email;
    private String password;
}
