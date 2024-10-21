package com.mycom.myapp.naviya.domain.book.entity;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Data
@Entity
@Table(name = "User_Recent_Books")
public class UserRecentBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_book_id")
    private Long recentBookId;

    @Column(name = "viewed_at")
    private Timestamp viewedAt;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @OneToOne(fetch = FetchType.LAZY)   
    @JoinColumn(name="book_id")
    private Book book;

    // Getters and Setters
}
