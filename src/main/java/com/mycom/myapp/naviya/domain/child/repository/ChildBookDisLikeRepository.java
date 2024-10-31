package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface ChildBookDisLikeRepository extends JpaRepository<ChildBookDislike, Long> {
    boolean existsByChild_ChildIdAndBook_BookId(Long childId, Long bookId);

    @Modifying
    @Transactional // 이 메서드에 트랜잭션이 필요합니다.
    @Query("DELETE FROM ChildBookDislike d WHERE d.child.childId = :childId AND d.book.bookId = :bookId AND d.deletedAt IS NULL")
    void deleteByChild_ChildIdAndBook_BookId(@Param("childId") Long childId, @Param("bookId") Long bookId);


    @Query("SELECT CASE WHEN COUNT(cbl) > 0 THEN true ELSE false END FROM ChildBookDislike cbl " +
            "WHERE cbl.child.childId = :childId AND cbl.book.bookId = :bookId AND cbl.deletedAt IS NULL")
    boolean CHildBookDislkeIsExistDelDateIsNull(@Param("childId") Long childId, @Param("bookId") Long bookId);

    @Modifying
    @Transactional
    @Query("UPDATE ChildBookDislike cbd SET cbd.deletedAt = :futureDate WHERE cbd.child = :child AND cbd.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChildBookDislike cbd WHERE cbd.child = :child AND cbd.deletedAt = :deleteAt")
    void deleteByChildAndDeletedAt(Long child, LocalDateTime deleteAt);


    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO child_book_dislike (child_id, book_id, deleted_at) 
    SELECT :childId, :bookId, NULL
    WHERE NOT EXISTS (
        SELECT 1 FROM child_book_dislike 
        WHERE child_id = :childId 
        AND book_id = :bookId 
        AND deleted_at IS NULL
    )
""", nativeQuery = true)
    int saveChildBookDislike(@Param("childId") Long childId, @Param("bookId") Long bookId);

}





