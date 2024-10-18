package com.mycom.myapp.naviya.domain.book.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_favor_total")
public class BookFavorTotal {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long book_favor_total_id;
    @OneToOne
    @JoinColumn(name="book_id")
    private Book book;

    @Column(name = "count")
    private Long count;

    // Getters and Setters
}