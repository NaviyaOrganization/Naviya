package com.mycom.myapp.naviya.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInsertDto {
    private Long bookId;
    private String title;
    private String summary;
    private String recommendedAge;
    private String publisher;
    private String author;
    private String fullStory;
    private String bookImage;
    private String category;
    private Integer eiType;
    private Integer snType;
    private Integer tfType;
    private Integer jpType;

}
