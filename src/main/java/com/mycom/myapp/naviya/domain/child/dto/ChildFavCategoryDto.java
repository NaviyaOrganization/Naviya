package com.mycom.myapp.naviya.domain.child.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChildFavCategoryDto {

    private Long childId;
    private String categoryCode;
    private Long childFavorCategoryWeight;
}
