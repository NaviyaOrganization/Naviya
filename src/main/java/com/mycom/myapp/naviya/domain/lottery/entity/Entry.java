package com.mycom.myapp.naviya.domain.lottery.entity;
import jakarta.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "entry")
public class Entry {
    @Id
    @Column(name = "entry_id")
    private String entryId;

    private String email;
    private String name;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Getters and Setters
}