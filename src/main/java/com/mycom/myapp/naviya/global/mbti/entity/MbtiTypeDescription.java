package com.mycom.myapp.naviya.global.mbti.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mbti_type_description")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MbtiTypeDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mbti_id", nullable = false)
    private MbtiType mbtiType;

    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    private String description;

    @Builder
    public MbtiTypeDescription(MbtiType mbtiType, String description) {
        this.mbtiType = mbtiType;
        this.description = description;
    }
}