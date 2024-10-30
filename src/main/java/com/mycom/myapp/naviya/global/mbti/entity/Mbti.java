package com.mycom.myapp.naviya.global.mbti.entity;

import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
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

    @Transient
    private int oldEiType;

    @Transient
    private int oldSnType;

    @Transient
    private int oldTfType;

    @Transient
    private int oldJpType;

    @OneToOne(mappedBy = "mbti")
    private ChildMbti childMbtis;

    @OneToOne(mappedBy = "mbti")
    private BookMbti bookMbti;

    // 엔티티가 로드될 때 기존 점수를 저장하고 클램핑 적용
    @PostLoad
    private void saveOldScores() {
        // 점수를 -100에서 100 사이로 제한
        clampScores();

        this.oldEiType = this.eiType;
        this.oldSnType = this.snType;
        this.oldTfType = this.tfType;
        this.oldJpType = this.jpType;
    }

    @PreUpdate
    private void saveNewScores(){
        clampScores();
    }

    // 점수를 -100에서 100 사이로 제한하는 메서드
    private void clampScores() {
        this.eiType = Math.max(-100, Math.min(100, this.eiType));
        this.snType = Math.max(-100, Math.min(100, this.snType));
        this.tfType = Math.max(-100, Math.min(100, this.tfType));
        this.jpType = Math.max(-100, Math.min(100, this.jpType));
    }

    // Getters and Setters
}


