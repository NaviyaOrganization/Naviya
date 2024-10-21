package com.mycom.myapp.naviya.domain.lottery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LotteryEntryRequest {
    private String name;  // 사용자 이름
    private String phone; // 사용자 전화번호
}
