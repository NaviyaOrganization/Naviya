package com.mycom.myapp.naviya.domain.book.entity;

import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "BookMbti")
public class BookMbti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmbti_id")
    private Long bookMbtiId;

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @OneToOne
    @JoinColumn(name = "mbti_id", nullable = false)
    private Mbti mbti;
}
