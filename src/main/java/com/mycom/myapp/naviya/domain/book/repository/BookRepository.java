package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import java.util.List;
import java.util.Optional;

@Repository
//현재 최신순 및 총 좋아요 많은 두개만 나이대 별로 가져오기 적용 다른 애들 아이가 좋아요,최근본책은 굳이 필요 없음
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findBybookId(Long bookId);
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
    List<BookDto> findAllBookDto();//다 가져오기

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " + // 수정된 부분
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN Child c ON c.childId = :childId " +
            "LEFT JOIN ChildBookLike like ON like.book.bookId = b.bookId AND like.child.childId = :childId " +
            "LEFT JOIN ChildBookDislike dislike ON dislike.book.bookId = b.bookId AND dislike.child.childId = :childId " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "WHERE b.recommendedAge = c.ChildAgeRange " +
            "AND like.likeId IS NULL AND dislike.dislikeBookId IS NULL " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType,f.count")
        //다 가져오는데 CHILD 나이와 좋아요 싫어요 다 고려
    List<BookDto> ChildAgefindAllBookDto(@Param("childId") long childId);


    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "LEFT JOIN ChildBookLike like ON like.book.bookId = b.bookId AND like.child.childId = :childId " +
            "LEFT JOIN ChildBookDislike dislike ON dislike.book.bookId = b.bookId AND dislike.child.childId = :childId " +
            "LEFT JOIN Child c ON c.childId = :childId " + // Child를 LEFT JOIN
            "WHERE like.likeId IS NULL AND dislike.dislikeBookId IS NULL " +
            "AND b.recommendedAge = c.ChildAgeRange " + // recommendedAge와 ChildAgeRange가 같음
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY b.createdAt desc")
    List<BookDto> findBookDtoDescCreateDate(@Param("childId") long childId);//새로 생긴 책 추천

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
            "ORDER BY b.createdAt desc ")
    List<BookDto> NoChildfindBookDtoDescCreateDate();//새로 생긴 책 추천이지만 애의 나이와 좋아요 고려 X

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "LEFT JOIN ChildBookLike like ON like.book.bookId = b.bookId AND like.child.childId = :childId " +
            "LEFT JOIN ChildBookDislike dislike ON dislike.book.bookId = b.bookId AND dislike.child.childId = :childId " +
            "LEFT JOIN Child c ON c.childId = :childId " + // Child를 LEFT JOIN
            "WHERE like.likeId IS NULL AND dislike.dislikeBookId IS NULL " +
            "AND b.recommendedAge = c.ChildAgeRange " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY f.count DESC")
    List<BookDto> findBookDescBookFavorCount(@Param("childId") long childId);//좋아요 카운트순

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
            "ORDER BY f.count DESC")
    List<BookDto> NoChildfindBookDescBookFavorCount();//좋아요 카운트순이지만 애 나이 좋아요 싫어요 고려 x

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
            "AND cbl.deletedAt  IS NULL " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count " +
            "ORDER BY b.createdAt DESC")
    List<BookDto> findBooksLikeByChildId(@Param("childId") Long childId);

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
    List<BookDto> findBooksByChildRecentRead(@Param("childId") Long childId);


        @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDetailDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count), " +
            "CASE WHEN EXISTS (SELECT 1 FROM ChildBookLike l WHERE l.book.bookId = b.bookId AND l.child.childId = :childId) THEN true ELSE false END, " +
            "CASE WHEN EXISTS (SELECT 1 FROM ChildBookDislike d WHERE d.book.bookId = b.bookId AND d.child.childId = :childId) THEN true ELSE false END) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "WHERE b.bookId = :bookId")
    BookDetailDto findBookDetailDtoByBookId(@Param("bookId") Long bookId, @Param("childId") Long childId);

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
            "AND cbl.deletedAt  IS NULL " +
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
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "WHERE (:searchType = 'title' AND LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(:searchType = 'author' AND LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(:searchType = 'publisher' AND LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count")
    List<BookDto> searchBooks(@Param("searchType") String searchType, @Param("keyword") String keyword);

    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "LEFT JOIN Child c ON c.childId = :childId " +
            "WHERE b.categoryCode IN :categoryCodes " +
            "AND b.recommendedAge = c.ChildAgeRange " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count")
    List<BookDto> findAllBookDtoByCategoryCodes(@Param("categoryCodes") List<String> categoryCodes,@Param("childId") long childId);
    @Query("SELECT new com.mycom.myapp.naviya.domain.book.dto.BookDto(b.bookId, " +
            "b.title, b.summary, b.recommendedAge, b.publisher, b.author, " +
            "b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "new com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto(m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType), " +
            "new com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto(f.count)) " +
            "FROM Book b " +
            "LEFT JOIN b.bookMbti bm " +
            "LEFT JOIN bm.mbti m " +
            "LEFT JOIN BookFavorTotal f ON f.book.bookId = b.bookId " +
            "LEFT JOIN Child c ON c.childId = :childId " +
            "WHERE b.categoryCode =:categoryCode " +
            "AND b.recommendedAge = c.ChildAgeRange " +
            "GROUP BY b.bookId, b.title, b.summary, b.recommendedAge, b.publisher, " +
            "b.author, b.createdAt, b.fullStory, b.bookImage, b.categoryCode, " +
            "m.mbtiId, m.eiType, m.snType, m.tfType, m.jpType, f.count")
    List<BookDto> findBookDtoOneCateogory(@Param("childId") long childId,@Param("categoryCode")String categoryCode);




}
