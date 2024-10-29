package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.dto.*;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookMbtiRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto;
import com.mycom.myapp.naviya.domain.child.entity.*;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final MbtiRepository mbtiRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    private final ChildBookDisLikeRepository childBookDisLikeRepository;
    private final BookFavorTotalRepository bookFavorTotalRepository;
    private final BookMbtiRepository bookMbtiRepository;
    private final ChildRepository childRepository;
    private final ChildFavorCategoryRepository childFavorCategoryRepository;
    private final ChildMbtiRepository childMbtiRepository;
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    private final String QUEUE_NAME = "likeDislikeQueue";


    @Override
    @Transactional
    public BookResultDto delBook(Long bookId) {
        BookResultDto bookResultDto = new BookResultDto();
        try {
            bookRepository.deleteById(bookId);
            bookResultDto.setSuccess("success");
        }catch(Exception e) {
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
        }
        return bookResultDto;
    }
    @Override
    public BookResultDto detailBook(Long bookId,Long childId) {
        // BookRepository의 findAll() 메서드를 통해 모든 Book을 가져옴

        BookResultDto bookResultDto = new BookResultDto();
        try {
            BookDetailDto bookDetailDto=bookRepository.findBookDetailDtoByBookId(bookId,childId);
            bookResultDto.setBookDetail(bookDetailDto);
            bookResultDto.setSuccess("success");
            return bookResultDto;

        } catch (EntityNotFoundException e) {
            bookResultDto.setSuccess("fail");
            System.err.println(e.getMessage());
            return bookResultDto;
        }
    }

    @Override
    public BookResultDto listBook() {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findAllBookDto();
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }

    @Override
    public BookResultDto updateBook(BookInsertDto bookInsertDto) {
        return null;
    }

    @Override
    public BookResultDto insertBook(BookInsertDto bookInsertDto) {
        return null;
    }

    @Override
    public BookResultDto ChildALLlistBook(Long childId) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.ChildAgefindAllBookDto(childId);
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }


    @Override
    public BookResultDto listbookOrderByCreateDate(long childId) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findBookDtoDescCreateDate(childId);
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    @Override
    public BookResultDto NoCHildlistbookOrderByCreateDate() {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.NoChildfindBookDtoDescCreateDate();
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    @Override
    public BookResultDto listBookChildFavor(long ChildId) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findBooksLikeByChildId(ChildId);
            bookResultDto.setBooks(bookDto);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    @Override
    public BookResultDto NoChildlistBookChildFavor() {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.NoChildfindBookDescBookFavorCount();
            bookResultDto.setBooks(bookDto);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }


    @Override
    public BookResultDto listBookFavorCount(long childId) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findBookDescBookFavorCount(childId);
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    public BookResultDto listBookChildRecntRead(long ChildId) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
        List<BookDto> bookDto = bookRepository.findBooksByChildRecentRead(ChildId);
        bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
        bookResultDto.setSuccess("success");
        return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    public void enqueueLike(Long childId, Long bookId, String type) {
        // 요청을 큐에 추가
        redisTemplate.opsForList().rightPush(QUEUE_NAME, new LikeDislikeTaskDto("like",childId, bookId,type));
    }
    public void enqueueDisLike(Long childId, Long bookId,String type) {
        // 요청을 큐에 추가
        redisTemplate.opsForList().rightPush(QUEUE_NAME, new LikeDislikeTaskDto("dislike",childId, bookId,type));
    }
    @Override
   @Transactional
    public BookResultDto ChildBookLike(long ChildId, long BookId,String Type) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            enqueueLike(ChildId,BookId,Type);

            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail4");
            return bookResultDto;
        }
    }


    public BookResultDto ChildBookDisLike(long ChildId, long BookId,String Type) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            enqueueDisLike(ChildId,BookId,Type);
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail4");
            return bookResultDto;
        }
    }
    /*
    모두 가중치 동적 조정법 적용
    <a> mbti 별 추천에 대한 싫어요는 가중치를 1.2를 곱해서 크게 빼준다.
    <b> 일반책은 0.7곱해서 나눠서 빼준다.
    <c>mbti 반대별 싫어요는 당연한거니 0.5 곱해서 빼준다.*/
    @Override
    public BookResultDto DelChildBookLike(long BookId, long ChildId) {
        BookResultDto bookResultDto=new BookResultDto();
        try {
            childBookLikeRepository.deleteByChildIdAndBookId(ChildId, BookId);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    /*
   JPA에서 delete 메서드를 호출하면, 존재하지 않는 엔티티에 대한 삭제 요청이 있을 때 특별한 예외를 발생시키지 않습니다.*/
    @Override
    public BookResultDto DelChildBookDisLike(long BookId, long ChildId) {
        BookResultDto bookResultDto=new BookResultDto();
        try {
            childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(ChildId, BookId);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    @Transactional
    @Override
    public BookResultDto LogicDelChildBookLike(long BookId, long ChildId) {
        BookResultDto bookResultDto=new BookResultDto();
        try {
            List<ChildBookLike> childBookLikes=childBookLikeRepository.findByBookIdAndChildId(ChildId, BookId);
            if (!childBookLikes.isEmpty()) {
                ChildBookLike childBookLike=childBookLikes.get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 30); // 현재 날짜에 30일 추가
                Timestamp futureDate = new Timestamp(calendar.getTimeInMillis());
                childBookLike.setDeletedAt(futureDate.toLocalDateTime());
                childBookLikeRepository.save(childBookLike);
                bookResultDto.setSuccess("success");
                return bookResultDto;
            }
            else
            {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }

    }

    @Override
    public List<BookDto> searchBooks(String searchType, String keyword) {
        return bookRepository.searchBooks(searchType, keyword);
    }

    @Override
    //카테고리 추천
    //나이대별로 들고와야함
    @Transactional
    public BookResultDto CategoryList(long childId) {
        BookResultDto bookResultDto=new BookResultDto();
        try {

            //비로그인들 걍 랜덤으로 보내버림
            if(childId==0)
            {
                List<BookDto> BookDtoList=bookRepository.findAllBookDto();
                bookResultDto.setBooks(BookDtoList);
                bookResultDto.setSuccess("success");
                return bookResultDto;
            }

            List<ChildFavCategoryDto> childFavorCategory = childFavorCategoryRepository.findFavCategoriesByChildId(childId);
            //쿼리 위해 존재
            List<String> categoryCodes = childFavorCategory.stream()
                    .map(ChildFavCategoryDto::getCategoryCode)
                    .collect(Collectors.toList());
            List<BookDto> booksInCategory = bookRepository.findAllBookDtoByCategoryCodes(categoryCodes,childId);
            double totalWeight = 0;

            // 가중치 합산
            for (ChildFavCategoryDto category : childFavorCategory) {
                Long weight = category.getChildFavorCategoryWeight();
                totalWeight += weight;
            }

            // 각 카테고리별 책 개수 계산
            Map<String, Integer> categoryBookCounts = new HashMap<>();
            int totalBooks = 10; // 총 가져올 책의 개수
            int sumOfBooks = 0;
            String lastCategory = null;
            Map<String, Integer> currentCounts = new HashMap<>();
            for (ChildFavCategoryDto category : childFavorCategory) {
                String categoryCode = category.getCategoryCode();
                Long weight = category.getChildFavorCategoryWeight();
                // 비율 계산
                double ratio = (weight / totalWeight) * totalBooks;
                // 비율을 반영한 책 개수 저장 (반올림)
                int bookCount = (int) Math.round(ratio);
                currentCounts.put(categoryCode, bookCount);
                sumOfBooks += bookCount;
                lastCategory = categoryCode;
            }
            //30 30 30 이럴때 3 3 4로 맞추기 위해
            if (sumOfBooks != totalBooks && lastCategory != null) {
                int difference = totalBooks - sumOfBooks;
                currentCounts.put(lastCategory, currentCounts.get(lastCategory) + difference);
            }

            int totalBooksCnt = 10;
            List<BookDto> selectedBooks = new ArrayList<>();

            for (BookDto book : booksInCategory) {
                String categoryCode = book.getCategory();
                Integer count = currentCounts.get(categoryCode);

                if (count != null && count >= 1) {
                    selectedBooks.add(book);
                    count--; // 책 개수 감소
                    currentCounts.put(categoryCode, count); // 감소된 개수 다시 저장
                }
                // 절대적으로 책 자체가 쿼리에서 10개 미만으로 왔을때 고민 중
              //  int idx=0;
              //  while (selectedBooks.size() >= totalBooksCnt) {
              //  }
            }
            bookResultDto.setSuccess("fail");
            bookResultDto.setBooks(selectedBooks);
            return bookResultDto;

        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    //굳이 두개로 안나눠도 sign으로 해도 되는데 결합도 생각해서 나눠 놨음
    @Override
    public BookResultDto CategoryLike(long childId,String Ctegory) {
        BookResultDto bookResultDto=new BookResultDto();
        try {

            ChildFavorCategory favorCategory = childFavorCategoryRepository
                    .findByChild_ChildIdAndCategoryCode(childId, Ctegory)
                    .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

            Long TempVal=favorCategory.getChildFavorCategoryWeight();
            Long newWeight = Math.max(0, Math.min(100, TempVal+10));
            favorCategory.setChildFavorCategoryWeight(newWeight);
            childFavorCategoryRepository.save(favorCategory);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    @Override
    public BookResultDto CategoryDisLike(long childId,String Ctegory) {
        BookResultDto bookResultDto=new BookResultDto();
        try {

            ChildFavorCategory favorCategory = childFavorCategoryRepository
                    .findByChild_ChildIdAndCategoryCode(childId, Ctegory)
                    .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

            Long TempVal=favorCategory.getChildFavorCategoryWeight();
            Long newWeight = Math.max(0, Math.min(100, TempVal));
            favorCategory.setChildFavorCategoryWeight(newWeight);
            childFavorCategoryRepository.save(favorCategory);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
    @Override
    public BookResultDto BookCategoryOne(long childId,String categoryId) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findBookDtoOneCateogory(categoryId,childId);
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
            return bookResultDto;
        }
    }
}
