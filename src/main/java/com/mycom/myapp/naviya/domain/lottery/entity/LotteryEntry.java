package com.mycom.myapp.naviya.domain.lottery.entity;
import com.mycom.myapp.naviya.domain.user.entity.User;
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
    private String phone;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public LotteryEntry(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
    }
}