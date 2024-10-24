package com.mycom.myapp.naviya.domain.child.entity;

import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ChildMbti")
public class ChildMbti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MbtiId")
    private Long mbtiId;

    @OneToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @OneToOne
    @JoinColumn(name = "mbti_id", nullable = false)
    private Mbti mbti;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 논리적 삭제를 위한 컬럼 추가
}
