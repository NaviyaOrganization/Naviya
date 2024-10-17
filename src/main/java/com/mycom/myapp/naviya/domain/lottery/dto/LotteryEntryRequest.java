package com.mycom.myapp.naviya.domain.lottery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LotteryEntryRequest {
    private String name;  // 사용자 이름
    private String email; // 사용자 이메일
}
