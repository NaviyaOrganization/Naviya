package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.common.entity.Code;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "child_favor_category")
public class ChildFavorCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_bookcategory_id")
    private Long childBookCategoryId;

    @Column(name = "child_favor_category_weight")
    private Long childFavorCategoryWeight;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name="category_code")
    private String categoryCode;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 논리적 삭제를 위한 컬럼 추가

    // Getters and Setters
}
