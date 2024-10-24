package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.entity.BookFavorTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
}
