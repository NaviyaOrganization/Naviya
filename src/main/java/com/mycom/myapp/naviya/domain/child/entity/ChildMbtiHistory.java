package com.mycom.myapp.naviya.domain.child.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ChildMbtiHistory")
public class ChildMbtiHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbti_history_id")
    private Long mbtiHistoryId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "code_new_mbti")
    private String codeNewMbti;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Getters and Setters
}
