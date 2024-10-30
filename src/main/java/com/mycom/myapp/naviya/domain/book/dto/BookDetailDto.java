package com.mycom.myapp.naviya.domain.book.dto;

import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDetailDto {
    private Long bookId;
    private String title;
    private String summary;
    private String recommendedAge;
    private String publisher;
    private String author;
    private LocalDateTime createdAt;
    private String fullStory;
    private String bookImage;
    private String category;
    private MbtiDto bookMbti;
    private BookFavorTotalDto booktotalfavor;
    private boolean isLiked;    // 좋아요 존재 여부
    private boolean isDisliked; // 싫어요 존재 여부
}
