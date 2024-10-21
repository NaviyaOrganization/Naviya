package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChildBookLikeRepository extends JpaRepository<ChildBookLike, Long> {
    boolean existsByChild_ChildIdAndBook_BookId(Long childId, Long bookId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChildBookLike cbl WHERE cbl.child.childId = :childId AND cbl.book.bookId = :bookId")
    void deleteByChildIdAndBookId(@Param("childId") Long childId, @Param("bookId") Long bookId);
}
