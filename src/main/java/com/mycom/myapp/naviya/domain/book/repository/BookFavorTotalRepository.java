package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.entity.BookFavorTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookFavorTotalRepository extends JpaRepository<BookFavorTotal, Long> {
    BookFavorTotal findByBook_BookId(Long bookId);
    @Modifying
    @Query("UPDATE BookFavorTotal b SET b.count = b.count + 1 WHERE b.book.id = :bookId")
    void incrementCountByBookId(Long bookId);

    @Modifying
    @Query("UPDATE BookFavorTotal b SET b.count = b.count -1 WHERE b.book.id = :bookId")
    void decrementCountByBookId(Long bookId);

    @Modifying
    @Query(value = "UPDATE BookFavorTotal bft " +
            "SET bft.count = bft.count + 1 " +
            "WHERE bft.book.bookId = :bookId " +
            "AND NOT EXISTS (SELECT 1 FROM ChildBookLike c " +
            "WHERE c.child.childId = :childId AND c.book.bookId = :bookId AND c.delDate IS NULL)",
            nativeQuery = true)
    void incrementCountIfNotLiked(@Param("childId") long childId, @Param("bookId") long bookId);



}
