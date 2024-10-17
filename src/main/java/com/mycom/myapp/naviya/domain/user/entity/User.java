package com.mycom.myapp.naviya.domain.user.entity;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String email;
    private String password;
    private String name;
    private String phone;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Child> children;

    // Getters and Setters
}
