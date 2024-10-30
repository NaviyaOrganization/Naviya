package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildFavorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChildFavorCategoryRepository extends JpaRepository<ChildFavorCategory, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ChildFavorCategory fc WHERE fc.child.childId = :childId")
    void deleteCategoryByChildId(Long childId);

    @Modifying
    @Transactional
    @Query("UPDATE ChildFavorCategory cfc SET cfc.deletedAt = :futureDate WHERE cfc.child = :child AND cfc.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChildFavorCategory cfc WHERE cfc.child.childId = :childId AND cfc.deletedAt = :deleteAt")
    void deleteByChildIdAndDeletedAt(Long childId, LocalDateTime deleteAt);


    @Query("SELECT new com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto(c.childId, cfc.categoryCode, cfc.childFavorCategoryWeight) " +
            "FROM ChildFavorCategory cfc " +
            "JOIN cfc.child c " +
            "WHERE c.childId = :childId")
    List<ChildFavCategoryDto> findFavCategoriesByChildId(@Param("childId") Long childId);
    Optional<ChildFavorCategory> findByChild_ChildIdAndCategoryCode(Long childId, String categoryCode);
    @Query("SELECT COUNT(c) > 0 FROM ChildFavorCategory c WHERE c.child.childId = :childId")
    boolean existsByChildId(@Param("childId") Long childId);
}