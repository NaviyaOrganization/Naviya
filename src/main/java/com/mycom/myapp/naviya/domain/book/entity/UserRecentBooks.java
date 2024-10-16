package com.mycom.myapp.naviya.domain.book.entity;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "User_Recent_Books")
public class UserRecentBooks {
    @Id
    @Column(name = "rcent_book_id")
    private String rcentBookId;

    @Column(name = "viewed_at")
    private Timestamp viewedAt;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    // Getters and Setters
}