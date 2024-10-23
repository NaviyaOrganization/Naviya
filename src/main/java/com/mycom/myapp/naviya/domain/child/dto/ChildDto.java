package com.mycom.myapp.naviya.domain.child.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
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
