package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.book.entity.UserRecentBooks;
import com.mycom.myapp.naviya.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "child")
public class Child {
    @Id
    @Column(name = "child_id")
    private long childId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "child_name")
    private String childName;

    @Column(name = "child_age")
    private String childAge;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToOne
    @JoinColumn(name = "code_mbti", referencedColumnName = "MbtiId")
    private ChildMbti mbti;

    @OneToMany(mappedBy = "child")
    private List<UserRecentBooks> recentBooks;

    @OneToMany(mappedBy = "child")
    private List<ChildBookLike> likedBooks;

    @OneToMany(mappedBy = "child")
    private List<ChildMbtiHistory> mbtiHistories;

    @OneToMany(mappedBy = "child")
    private List<ChildFavorCategory> favorCategories;

    // Getters and Setters
}