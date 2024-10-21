package com.mycom.myapp.naviya.global.mbti.Dto;

import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MbtiDto {

    private Long mbtiId;
    private int eiType;
    private int snType;
    private int tfType;
    private int jpType;
    /*public MbtiDto(){}
    public MbtiDto(Long mbtiId, int eiType, int snType, int tfType, int jpType) {
        this.mbtiId = mbtiId;
        this.eiType = eiType;
        this.snType = snType;
        this.tfType = tfType;
        this.jpType = jpType;
    }*/

}
