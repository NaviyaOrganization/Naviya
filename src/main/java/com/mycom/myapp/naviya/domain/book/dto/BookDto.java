package com.mycom.myapp.naviya.domain.book.dto;

import com.mycom.myapp.naviya.domain.book.entity.UserRecentBooks;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
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
    private Mbti bookMbti;
}
