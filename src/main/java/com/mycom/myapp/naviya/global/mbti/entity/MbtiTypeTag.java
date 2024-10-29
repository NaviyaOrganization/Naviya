package com.mycom.myapp.naviya.global.mbti.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mbti_type_tag")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MbtiTypeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mbti_id", nullable = false)
    private MbtiType mbtiType;

    @Column(name = "tag", length = 10, nullable = true)
    private String tag;

    @Builder
    public MbtiTypeTag(MbtiType mbtiType, String tag) {
        this.mbtiType = mbtiType;
        this.tag = tag;
    }
}


