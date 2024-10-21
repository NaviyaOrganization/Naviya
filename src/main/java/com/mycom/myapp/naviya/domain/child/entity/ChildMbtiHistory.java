package com.mycom.myapp.naviya.domain.child.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Entity
@Data
@Table(name = "ChildMbtiHistory")
public class ChildMbtiHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbti_history_id")
    private Long mbtiHistoryId;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "code_new_mbti")
    private String codeNewMbti;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    // Getters and Setters
}
