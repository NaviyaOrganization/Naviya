package com.mycom.myapp.naviya.domain.book.dto;

import com.mycom.myapp.naviya.domain.book.entity.UserRecentBooks;
import com.mycom.myapp.naviya.domain.category.dto.CategoryBookDto;
import com.mycom.myapp.naviya.domain.category.entity.BookCategory;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
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
    private long count;
    private String eiType;
    private String snType;
    private String tfType;
    private String jpType;
    //private BookCategory category;
    //private List<UserRecentBooks> recentBooks;
    //private List<ChildBookLike> likedBooks;
    private CategoryBookDto categoryBookDto;
    private Timestamp createDate;
}
