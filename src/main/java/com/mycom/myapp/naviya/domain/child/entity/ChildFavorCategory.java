package com.mycom.myapp.naviya.domain.child.entity;
import com.mycom.myapp.naviya.domain.category.entity.BookCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "child_favor_category")
public class ChildFavorCategory {
    @Id
    @Column(name = "child_bookcategory_id")
    private String childBookCategoryId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private BookCategory category;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    // Getters and Setters
}