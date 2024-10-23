package com.mycom.myapp.naviya.domain.child.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChildDto {

    private Long childId;
    private Long userId;
    private String childName;
    private int childAge;
    private Character childGender;
    private String codeMbti;
    private String childImage;
}
