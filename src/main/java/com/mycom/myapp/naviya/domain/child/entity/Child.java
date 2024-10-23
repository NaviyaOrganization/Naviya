package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.book.entity.UserRecentBooks;
import com.mycom.myapp.naviya.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
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
    private String childAge;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "code_mbti")
    private String codeMbti;

    @Column(name = "child_image")
    private String childImage;
    @OneToMany(mappedBy = "child")
    private List<ChildBookDislike> childBookDislikes;
    @OneToMany(mappedBy = "child")
    private List<ChildBookLike> chldBookLikes;
    @OneToOne(mappedBy = "child")
    private ChildMbti childMbti;
    @Column(name="child_age_range")
    private String ChildAgeRange;
}
