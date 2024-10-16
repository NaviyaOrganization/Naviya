package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Entity
@Data
@Table(name = "child_book_like")
public class ChildBookLike {
    @Id
    @Column(name = "like_id")
    private String likeId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    // Getters and Setters
}