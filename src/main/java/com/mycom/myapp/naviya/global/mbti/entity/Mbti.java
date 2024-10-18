package com.mycom.myapp.naviya.global.mbti.entity;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Mbti")
public class Mbti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbti_id")
    private Long mbtiId;

    @Column(name = "ei_type")
    private int eiType;

    @Column(name = "sn_type")
    private int snType;

    @Column(name = "tf_type")
    private int tfType;

    @Column(name = "jp_type")
    private int jpType;

    @OneToOne(mappedBy = "mbti")
    private ChildMbti childMbtis;

    @OneToOne(mappedBy = "mbti")
    private BookMbti bookMbti;

    // Getters and Setters
}

