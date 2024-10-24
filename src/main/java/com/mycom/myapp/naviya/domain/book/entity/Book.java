package com.mycom.myapp.naviya.domain.book.entity;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookDislike;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "recommended_age")
    private String recommendedAge;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "author")
    private String author;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "full_story", columnDefinition = "TEXT")
    private String fullStory;

    @Column(name = "book_image", columnDefinition = "BLOB")
    private byte[] bookImage;

    @Column(name = "category_code")
    private String categoryCode;

    @OneToOne(cascade =CascadeType.ALL ,mappedBy = "book")
    private UserRecentBooks userRecentBooks;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "book")
    private List<ChildBookLike> childBookLike;


    @OneToOne(cascade =CascadeType.ALL ,mappedBy = "book")
    private BookMbti bookMbti;

    @OneToOne(mappedBy = "book")
    private ChildBookDislike childBookDislike;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "book")
    private BookFavorTotal bookFavorTotal;
}
