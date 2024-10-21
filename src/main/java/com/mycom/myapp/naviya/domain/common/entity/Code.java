package com.mycom.myapp.naviya.domain.common.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "code")
@Data
public class Code {

    @Id
    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code")
    private GroupCode groupCode;

    @Column(name = "code_name")
    private String codeName;

    @Column(name = "order_no")
    private int orderNo;
}
