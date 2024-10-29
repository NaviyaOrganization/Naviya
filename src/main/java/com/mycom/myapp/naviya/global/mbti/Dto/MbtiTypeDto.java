package com.mycom.myapp.naviya.global.mbti.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON 결과에 포함되지 않도록 설정
@Builder
@AllArgsConstructor
public class MbtiTypeDto {

    @JsonProperty("type") // JSON 직렬화 시 "type"이라는 이름으로 표현
    private String type;

    @JsonProperty("image")
    private String image;

    @JsonProperty("descriptions")
    private Set<MbtiTypeDescriptionDto> descriptions;

    @JsonProperty("tags")
    private Set<MbtiTypeTagDto> tags;

}

