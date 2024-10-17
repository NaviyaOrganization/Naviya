package com.mycom.myapp.naviya.domain.child.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ChildMbti")
@Data
public class ChildMbti {
    @Id
    @Column(name = "MbtiId")
    private Long mbtiId;

    @Column(name = "ei_type")
    private String eiType;

    @Column(name = "sn_type")
    private String snType;

    @Column(name = "tf_type")
    private String tfType;

    @Column(name = "jp_type")
    private String jpType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child_id")
    private Child child;

    // Getters and Setters
}