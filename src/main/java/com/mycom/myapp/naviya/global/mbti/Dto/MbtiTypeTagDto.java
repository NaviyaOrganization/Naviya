package com.mycom.myapp.naviya.global.mbti.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON 결과에 포함되지 않도록 설정
public class MbtiTypeTagDto {

    @JsonProperty("tag")
    private String tag;

    @Builder
    public MbtiTypeTagDto(String tag) {
        this.tag = tag;
    }
}
