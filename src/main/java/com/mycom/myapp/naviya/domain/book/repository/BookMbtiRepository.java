package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookMbtiRepository extends JpaRepository<BookMbti, Long> {

    //@Query("SELECT b.mbti FROM BookMbti b WHERE b.book.bookId = :bookId")
    //List<Mbti> findMbtiByBookId(@Param("bookId") Long bookId);

    BookMbti findByBook_BookId(Long bookId);
}
