package com.mycom.myapp.naviya.domain.lottery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LotteryEntryRequest {

    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;  // 사용자 이름

    @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 11자리 숫자여야 합니다.")
    private String phone; // 사용자 전화번호
}
