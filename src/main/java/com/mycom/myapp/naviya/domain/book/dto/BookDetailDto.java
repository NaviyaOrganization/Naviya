package com.mycom.myapp.naviya.domain.book.dto;

import com.mycom.myapp.naviya.domain.book.entity.UserRecentBooks;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.global.mbti.Mbti_Name;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
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
    private Timestamp createdAt;
    private String fullStory;
    private byte[] bookImage;
    private String category;
    private MbtiDto bookMbti;
    private BookFavorTotalDto booktotalfavor;
    private boolean isLiked;    // 좋아요 존재 여부
    private boolean isDisliked; // 싫어요 존재 여부
}
