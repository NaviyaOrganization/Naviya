package com.mycom.myapp.naviya.domain.child.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MBTIScoresDto {
    private String eiScore;  // "I" 또는 "E"
    private String snScore;  // "S" 또는 "N"
    private String tfScore;  // "T" 또는 "F"
    private String jpScore;  // "J" 또는 "P"
}
