package com.mycom.myapp.naviya.domain.child.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChildAddDto {

    private ChildDto childDto;
    private List<ChildFavCategoryDto> childFavCategoryDtoList;
}
