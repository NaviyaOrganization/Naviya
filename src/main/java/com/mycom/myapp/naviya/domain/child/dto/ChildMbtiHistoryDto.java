package com.mycom.myapp.naviya.domain.child.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChildMbtiHistoryDto {
    private Long mbtiHistoryId;
    private LocalDateTime updatedAt;
    private String codeNewMbti;  // 진단된 MBTI 코드
}