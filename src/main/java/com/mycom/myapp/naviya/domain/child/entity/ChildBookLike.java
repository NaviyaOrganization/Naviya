package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "child_book_like")
public class ChildBookLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 논리적 삭제를 위한 컬럼 추가
    // Getters and Setters
}