package com.mycom.myapp.naviya.domain.child.entity;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 논리적 삭제를 위한 컬럼 추가

    // Getters and Setters
}