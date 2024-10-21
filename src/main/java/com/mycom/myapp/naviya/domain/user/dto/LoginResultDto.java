package com.mycom.myapp.naviya.domain.user.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResultDto extends SignupResultDto{

    private String token;

//    @Builder
//    public LoginResultDto(boolean success, int code, String msg, String token) {
//        super(success, code, msg);
//        this.token = token;
//    }

}