package com.mycom.myapp.naviya.domain.lottery.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "lottery_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LotteryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String status; // 응모 상태: pending, success, fail

    @Column(name = "created_at")
    private Timestamp createdAt;

    public LotteryEntry(String name, String email) {
        this.name = name;
        this.email = email;
        this.status = "pending";
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }
}