package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "child")
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long childId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "child_name")
    private String childName;

    @Column(name = "child_age")
    private int childAge;

    @Column(name = "child_gender")
    private Character childGender;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "code_mbti")
    private String codeMbti;

    @Column(name = "child_image")
    private String childImage;

    @Column(name="child_age_range")
    private String ChildAgeRange;

    @OneToMany(mappedBy = "child")
    private List<ChildBookDislike> childBookDislikes;

    @OneToMany(mappedBy = "child")
    private List<ChildBookLike> chldBookLikes;

    @OneToOne(mappedBy = "child")
    private ChildMbti childMbti;

    @OneToMany(mappedBy = "child")
    private List<ChildFavorCategory> childFavorCategories = new ArrayList<>();

}
