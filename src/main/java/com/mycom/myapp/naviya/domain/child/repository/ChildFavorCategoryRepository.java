package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildFavorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface ChildFavorCategoryRepository extends JpaRepository<ChildFavorCategory, Long> {

    @Query("DELETE FROM ChildFavorCategory fc WHERE fc.child.childId = :childId")
    void deleteCategoryByChildId(Long childId);

    @Modifying
    @Transactional
    @Query("UPDATE ChildFavorCategory cfc SET cfc.deletedAt = :futureDate WHERE cfc.child = :child AND cfc.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChildFavorCategory cfc WHERE cfc.child = :child AND cfc.deletedAt = :deleteAt")
    void deleteByChildAndDeletedAt(Child child, LocalDateTime deleteAt);

}