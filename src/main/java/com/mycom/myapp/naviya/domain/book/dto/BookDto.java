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
  /*  public BookDto() {}
    public BookDto(Long bookId, String title, String summary, String recommendedAge, String publisher, String author,
                   Timestamp createdAt, String fullStory, byte[] bookImage, String categoryCode, MbtiDto bookMbti,
                   Long booktotalfavor) {
        this.bookId = bookId;
        this.title = title;
        this.summary = summary;
        this.recommendedAge = recommendedAge;
        this.publisher = publisher;
        this.author = author;
        this.createdAt = createdAt;
        this.fullStory = fullStory;
        this.bookImage = bookImage;
        this.category = categoryCode;
        this.bookMbti = bookMbti;
        this.booktotalfavor = booktotalfavor;
    }*/
}
