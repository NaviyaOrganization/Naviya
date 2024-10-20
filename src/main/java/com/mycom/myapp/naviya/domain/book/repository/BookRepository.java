package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import java.util.List;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " + // 수정된 부분
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType,f.count")
    List<BookDto> findAllBookDto();
    Book findBybookId(Long bookId);

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY b.createdAt ASC")  // createdAt 기준 오름차순 정렬
    List<BookDto> findBookDtoDescCreateDate();

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY f.count DESC")  // f.count 기준 내림차순 정렬
    List<BookDto> findBookDescBookFavor();

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM ChildBookLike cbl " +
            "JOIN cbl.book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "WHERE cbl.child.childId = :childId " +
            "AND cbl.DelDate IS NULL " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY b.createdAt DESC")

    List<BookDto> findBooksByChildId(@Param("childId") Long childId);

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM UserRecentBooks ur " +
            "JOIN ur.book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "WHERE ur.child.childId = :childId " +  // 특정 child_id를 기준으로 필터링
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY b.createdAt DESC")  // createdAt DESC로 정렬
    List<BookDto> findBooksByChildIdOrderByCreatedAtDesc(@Param("childId") Long childId);
}
