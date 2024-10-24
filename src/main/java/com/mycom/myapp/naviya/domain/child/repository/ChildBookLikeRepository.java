package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ChildBookLikeRepository extends JpaRepository<ChildBookLike, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ChildBookLike cbl WHERE cbl.child.childId = :childId AND cbl.book.bookId = :bookId")
    void deleteByChildIdAndBookId(@Param("childId") Long childId, @Param("bookId") Long bookId);

    @Query("SELECT cbl FROM ChildBookLike cbl WHERE cbl.book.bookId = :bookId AND cbl.child.childId = :childId AND cbl.deletedAt IS NULL")
    List<ChildBookLike> findByBookIdAndChildId(@Param("bookId") long bookId, @Param("childId") long childId);

    @Query("SELECT CASE WHEN COUNT(cbl) > 0 THEN true ELSE false END FROM ChildBookLike cbl " +
            "WHERE cbl.child.childId = :childId AND cbl.book.bookId = :bookId AND cbl.deletedAt IS NULL")
    boolean existsByChildIdAndBookIdAndDelDateIsNull(@Param("childId") Long childId, @Param("bookId") Long bookId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChildBookLike c " +
            "WHERE c.child.childId = :childId " +
            "AND c.book.bookId = :bookId " +
            "AND c.deletedAt IS NULL")
    void deleteByChildIdAndBookIdAndDelDateIsNull(@Param("childId") Long childId, @Param("bookId") Long bookId);

    @Modifying
    @Transactional
    @Query("UPDATE ChildBookLike cbl SET cbl.deletedAt = :futureDate WHERE cbl.child = :child AND cbl.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChildBookLike cbl WHERE cbl.child = :child AND cbl.deletedAt = :deleteAt")
    void deleteByChildAndDeletedAt(Child child, LocalDateTime deleteAt);
}
