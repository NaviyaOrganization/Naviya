package com.mycom.myapp.naviya.domain.common.entity;
import jakarta.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "GroupCode")
public class GroupCode {
    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "group_code_name")
    private String groupCodeName;

    // Getters and Setters
}
