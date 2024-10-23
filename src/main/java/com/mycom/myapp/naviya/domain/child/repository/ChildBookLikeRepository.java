package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChildBookLikeRepository extends JpaRepository<ChildBookLike, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ChildBookLike cbl WHERE cbl.child.childId = :childId AND cbl.book.bookId = :bookId")
    void deleteByChildIdAndBookId(@Param("childId") Long childId, @Param("bookId") Long bookId);
    @Query("SELECT cbl FROM ChildBookLike cbl WHERE cbl.book.bookId = :bookId AND cbl.child.childId = :childId AND cbl.DelDate IS NULL")
    List<ChildBookLike> findByBookIdAndChildId(@Param("bookId") long bookId, @Param("childId") long childId);

    @Query("SELECT CASE WHEN COUNT(cbl) > 0 THEN true ELSE false END FROM ChildBookLike cbl " +
            "WHERE cbl.child.childId = :childId AND cbl.book.bookId = :bookId AND cbl.DelDate IS NULL")
    boolean existsByChildIdAndBookIdAndDelDateIsNull(@Param("childId") Long childId, @Param("bookId") Long bookId);


    @Modifying
    @Transactional
    @Query("DELETE FROM ChildBookLike c " +
            "WHERE c.child.childId = :childId " +
            "AND c.book.bookId = :bookId " +
            "AND c.DelDate IS NULL")
    void deleteByChildIdAndBookIdAndDelDateIsNull(@Param("childId") Long childId, @Param("bookId") Long bookId);
}
