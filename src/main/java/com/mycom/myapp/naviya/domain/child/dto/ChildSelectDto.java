package com.mycom.myapp.naviya.domain.child.dto;

import lombok.Data;

@Data
public class ChildSelectDto {

    private Long childId;
    private String childName;
    private String childImage;

    public ChildSelectDto(Long childId, String childName, String childImage) {
        this.childId = childId;
        this.childName = childName;
        this.childImage = childImage;
    }
}
