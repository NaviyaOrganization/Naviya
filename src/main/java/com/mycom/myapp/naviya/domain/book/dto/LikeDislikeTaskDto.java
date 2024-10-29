package com.mycom.myapp.naviya.domain.book.dto;

import lombok.Data;

@Data
public class LikeDislikeTaskDto{
    private String method; // 메소드 이름
    private Long childId;
    private Long bookId;
    private String Type;
    public LikeDislikeTaskDto() {
    }
    public LikeDislikeTaskDto(String method, Long childId, Long bookId,String Type) {
    this.method = method;
    this.childId = childId;
    this.bookId = bookId;
    this.Type = Type;
    }
}
