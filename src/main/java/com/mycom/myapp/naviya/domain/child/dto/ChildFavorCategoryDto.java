package com.mycom.myapp.naviya.domain.child.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildFavorCategoryDto {
    private Long childBookCategoryId;
    private Long childFavorCategoryWeight;
    private String categoryCode;

}
