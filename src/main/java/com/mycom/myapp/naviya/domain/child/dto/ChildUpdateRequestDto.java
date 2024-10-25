package com.mycom.myapp.naviya.domain.child.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChildUpdateRequestDto {
    private ChildDto childDto;
    private List<String> categoryCodeList;
}
