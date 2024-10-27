package com.mycom.myapp.naviya.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInsertDto {
    private String title;
    private String summary;
    private String recommendedAge;
    private String publisher;
    private String author;
    private String fullStory;
    private String bookImage;
    private String category;
    private int eiType;
    private int snType;
    private int tfType;
    private int jpType;

}
