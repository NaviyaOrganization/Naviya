package com.mycom.myapp.naviya.domain.book.entity;

import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "BookMbti")
/*
Book (부모)↔ BookMbti (자식)BookMbti↔ Mbti (부모)
 book에  cascaed ->bookmbti 자식에서 -> mbti에게 cascaed으로
일대일 양방향에서 자식이 부모에게 casecaed 걸 수 있음
기술적으로 가능은함
우리는 일부로 childmbti와 bookmbti를 동시에 사용하기 때문에 자식에게 casecade를 걸어도 무방
*/
public class BookMbti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmbti_id")
    private Long bookMbtiId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mbti_id", nullable = false)
    private Mbti mbti;
}
