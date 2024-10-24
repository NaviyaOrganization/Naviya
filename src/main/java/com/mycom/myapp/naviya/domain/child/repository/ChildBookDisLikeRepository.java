package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChildBookDisLikeRepository extends JpaRepository<ChildBookDislike, Long> {
    boolean existsByChild_ChildIdAndBook_BookId(Long childId, Long bookId);

    @Modifying
    @Transactional // 이 메서드에 트랜잭션이 필요합니다.
    @Query("DELETE FROM ChildBookDislike d WHERE d.child.childId = :childId AND d.book.bookId = :bookId")
    void deleteByChild_ChildIdAndBook_BookId(@Param("childId") Long childId, @Param("bookId") Long bookId);

    @Modifying
    @Transactional
    @Query("UPDATE ChildBookDislike cbd SET cbd.deletedAt = :futureDate WHERE cbd.child = :child AND cbd.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChildBookDislike cbd WHERE cbd.child = :child AND cbd.deletedAt = :deleteAt")
    void deleteByChildAndDeletedAt(Child child, LocalDateTime deleteAt);



}





