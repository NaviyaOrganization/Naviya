package com.mycom.myapp.naviya.domain.lottery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
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
    private String phone;

    @Column(nullable = false)
    private String selectedProgram;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp createdAt;

    public LotteryEntry(String name, String phone, String selectedProgram) {
        this.name = name;
        this.phone = phone;
        this.selectedProgram = selectedProgram;
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

    // 기존 생성자 유지 (하위 호환성)
    public LotteryEntry(String name, String phone) {
        this(name, phone, "미선택");
    }
}