package com.mycom.myapp.naviya.domain.child.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChildWithMbtiHistoryDto {
    private ChildDto child;  // 아이 정보
    private List<ChildMbtiHistoryDto> mbtiHistory;  // MBTI 히스토리 리스트
}