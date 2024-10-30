package com.mycom.myapp.naviya.domain.child.entity;

import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "child_mbti")
public class ChildMbti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "childmbti_id") // 기본 키이자 AUTO_INCREMENT 컬럼
    private Long mbtiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mbti_id", nullable = false)
    private Mbti mbti;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 논리적 삭제를 위한 컬럼 추가

}
