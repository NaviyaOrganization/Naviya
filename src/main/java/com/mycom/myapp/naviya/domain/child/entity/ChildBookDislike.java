package com.mycom.myapp.naviya.domain.child.entity;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "child_book_dislike")
public class ChildBookDislike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dislike_book_id")
    private Long dislikeBookId;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Getters and Setters
}