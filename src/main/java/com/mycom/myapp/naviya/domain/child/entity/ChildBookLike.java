package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.sql.Timestamp;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "del_date")
    private Timestamp DelDate;
    // Getters and Setters
}