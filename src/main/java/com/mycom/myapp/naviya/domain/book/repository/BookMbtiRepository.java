package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookMbtiRepository extends JpaRepository<BookMbti, Long> {

    //@Query("SELECT b.mbti FROM BookMbti b WHERE b.book.bookId = :bookId")
    //List<Mbti> findMbtiByBookId(@Param("bookId") Long bookId);

    BookMbti findByBook_BookId(Long bookId);

    @Query("SELECT new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(bm.bookMbtiId, m.eiType, m.snType, m.tfType, m.jpType) " +
            "FROM BookMbti bm " +
            "JOIN bm.mbti m " +
            "WHERE bm.book.bookId = :bookId")
    Optional<MbtiDto> findMbtiDtoByBookId(@Param("bookId") Long bookId);
}
