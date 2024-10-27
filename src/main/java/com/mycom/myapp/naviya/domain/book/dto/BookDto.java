package com.mycom.myapp.naviya.domain.book.dto;

import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDto {
    private Long bookId;
    private String title;
    private String summary;
    private String recommendedAge;
    private String publisher;
    private String author;
    private Timestamp createdAt;
    private String fullStory;
    private byte[] bookImage;
    private String category;
    private MbtiDto bookMbti;
    private BookFavorTotalDto booktotalfavor;

}
