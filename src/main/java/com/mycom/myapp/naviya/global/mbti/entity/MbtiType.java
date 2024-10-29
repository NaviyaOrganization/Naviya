package com.mycom.myapp.naviya.global.mbti.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "mbti_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MbtiType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", length = 4, nullable = true)
    private String type;

    @Lob
    @Column(name = "image", nullable = true)
    private String image;

    @OneToMany(mappedBy = "mbtiType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MbtiTypeDescription> descriptions = new HashSet<>();

    @OneToMany(mappedBy = "mbtiType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MbtiTypeTag> tags = new HashSet<>();

}
