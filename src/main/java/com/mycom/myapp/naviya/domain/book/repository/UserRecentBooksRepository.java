package com.mycom.myapp.naviya.domain.book.repository;
import com.mycom.myapp.naviya.domain.book.entity.UserRecentBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRecentBooksRepository extends JpaRepository<UserRecentBooks, Long> {

    @Query("SELECT CASE WHEN COUNT(urb) > 0 THEN true ELSE false END " +
            "FROM UserRecentBooks urb " +
            "WHERE urb.child.childId = :childId AND urb.book.bookId = :bookId")
    boolean existsByChildIdAndBookId(@Param("childId") Long childId, @Param("bookId") Long bookId);
}