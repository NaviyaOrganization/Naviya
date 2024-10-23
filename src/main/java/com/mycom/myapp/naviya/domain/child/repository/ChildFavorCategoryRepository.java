package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.ChildFavorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChildFavorCategoryRepository extends JpaRepository<ChildFavorCategory, Long> {

    @Query("DELETE FROM ChildFavorCategory fc WHERE fc.child.childId = :childId")
    void deleteCategoryByChildId(Long childId);
}
