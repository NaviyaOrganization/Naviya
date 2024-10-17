package com.mycom.myapp.naviya.domain.book.entity;
import com.mycom.myapp.naviya.domain.category.entity.BookCategory;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "Book")
@Data
public class Book {
    @Id
    @Column(name = "book_id")
    private Long bookId;

    private String title;
    private String summary;

    @Column(name = "recommended_age")
    private String recommendedAge;

    private String publisher;
    private String author;
    private long count;

    @Column(name = "ei_type")
    private String eiType;

    @Column(name = "sn_type")
    private String snType;

    @Column(name = "tf_type")
    private String tfType;

    @Column(name = "jp_type")
    private String jpType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private BookCategory category;

    @OneToMany(mappedBy = "book")
    private List<UserRecentBooks> recentBooks;

    @OneToMany(mappedBy = "book")
    private List<ChildBookLike> likedBooks;

    @Column(name = "create_date")
    private Timestamp createDate;
    // Getters and Setters
}