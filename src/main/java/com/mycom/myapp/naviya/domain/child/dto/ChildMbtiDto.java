package com.mycom.myapp.naviya.domain.child.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChildMbtiDto {
    private String childName;
    private String codeMbti;
    private int eiType;
    private int snType;
    private int tfType;
    private int jpType;
}