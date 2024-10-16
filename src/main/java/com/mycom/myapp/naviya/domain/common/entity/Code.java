package com.mycom.myapp.naviya.domain.common.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Code")
//@IdClass(CodeId.class) 추후 협의

public class Code {
    @Id
    @Column(name = "group_code")
    private String groupCode;

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "code_name")
    private String codeName;

    // Getters and Setters
}