package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
import com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.entity.BookFavorTotal;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookMbtiRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookDislike;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookDisLikeRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookLikeRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildMbtiRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookSerive {

    private final BookRepository bookRepository;
    private final MbtiRepository mbtiRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    private final ChildBookDisLikeRepository childBookDisLikeRepository;
    private final BookFavorTotalRepository bookFavorTotalRepository;
    private final BookMbtiRepository bookMbtiRepository;
    private final ChildRepository childRepository;
    private final ChildMbtiRepository childMbtiRepository;
    /*삭제순서 to 유성,정슈선
    연관관계 수정으로 book->bookmbti->mbti 한 방에 삭제

    Book (부모)↔ BookMbti (자식) ↔ Mbti (부모)
     book에  cascaed ->bookmbti 자식에서 -> mbti에게 cascaed으로
    일대일 양방향에서 자식이 부모에게 casecaed 걸 수 있음
    기술적으로 가능은함
    우리는 일부로 childmbti와 bookmbti를 동시에 사용하기 때문에 자식에게 casecade를 걸어도 무방
      */
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

    //to 유성 일단 서비스에서는 mbti 값 그대로 내리고 프론트  or 컨트롤러에서 추가 구현 및 조정 생각하고 구현해놨음
    //추후 구현방식 협의 후 수정 가능
    //일대일 관계에서는 lazy를 걸어도 eager로 적용되어
    //n+1 이슈로 밀고 dto쿼리로 바꿨음
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
    public BookResultDto updateBook(BookDto bookDto) {
        return null;
    }

    @Override
    public BookResultDto insertBook(BookDto bookDto) {
        return null;
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
    @Override
    @Transactional
    public BookResultDto ChildBookLike(long BookId, long ChildId,String Type) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            Optional child=childRepository.findById(ChildId);
            Child child1=new Child();
            if(child.isPresent()){
                child1=(Child)child.get();
            }
            else
            {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
            Optional book=bookRepository.findById(BookId);
            Book book1=new Book();
            if(book.isPresent()){
                book1=(Book)book.get();
            }
            else
            {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
            ChildMbti childMbti = child1.getChildMbti();
            if (childMbti == null) {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
            if (childBookDisLikeRepository.existsByChild_ChildIdAndBook_BookId(ChildId, BookId)) {
                childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(ChildId, BookId);
            }
            BookMbti bookMbti = book1.getBookMbti();
            Mbti book2Mbti=new Mbti();
            book2Mbti=bookMbti.getMbti();
            Mbti mbti = childMbti.getMbti();
            int EI;
            int SN;
            int TF;
            int JP;
            if (Type!=null &&Objects.equals(Type, "MBTI"))
            {
                EI= (int) (book2Mbti.getEiType()*1.5);
                SN= (int) (book2Mbti.getSnType()*1.5);
                TF= (int) (book2Mbti.getTfType()*1.5);
                JP= (int) (book2Mbti.getJpType()*1.5);
            } else if (Type!=null &&Objects.equals(Type, "NOMAL")) {
                EI= book2Mbti.getEiType();
                SN= book2Mbti.getSnType();
                TF= book2Mbti.getTfType();
                JP= book2Mbti.getJpType();
            }
            else
            {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }

            ChildBookLike childBookLike = new ChildBookLike();
            childBookLike.setChild(child1);
            childBookLike.setBook(book1);
            childBookLike.setDelDate(null);
            childBookLikeRepository.save(childBookLike);

            mbti.setEiType(EI+mbti.getEiType());
            mbti.setSnType(SN+mbti.getSnType());
            mbti.setTfType(TF+mbti.getTfType());
            mbti.setJpType(JP+mbti.getJpType());
            mbtiRepository.save(mbti);
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
    @Transactional
    public BookResultDto ChildBookDisLike(long BookId, long ChildId,String Type) {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            Optional child=childRepository.findById(ChildId);
            Child child1=new Child();
            if(child.isPresent()){
                child1=(Child)child.get();
            }
            else
            {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
            Optional book=bookRepository.findById(BookId);
            Book book1=new Book();
            if(book.isPresent()){
                book1=(Book)book.get();
            }
            else
            {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
            ChildMbti childMbti = child1.getChildMbti();
            if (childMbti == null) {
                bookResultDto.setSuccess("fail");
                return bookResultDto;
            }
            if (childBookLikeRepository.existsByChildIdAndBookIdAndDelDateIsNull(ChildId, BookId)) {
                childBookLikeRepository.deleteByChildIdAndBookId(ChildId, BookId);
            }
            BookMbti bookMbti = bookMbtiRepository.findByBook_BookId(BookId);
            Mbti book2Mbti=new Mbti();
            book2Mbti=bookMbti.getMbti();
            Mbti mbti = childMbti.getMbti();
            int EI=0;
            int SN=0;
            int TF=0;
            int JP=0;
            //비율부터 정한다음
            //mbti <-동적 가중치
            //mbti 반대 <-많이 줌

            if (Type!=null &&Objects.equals(Type, "MBTI"))
            {
                EI= (int) (book2Mbti.getEiType()*1.5);
                SN= (int) (book2Mbti.getSnType()*1.5);
                TF= (int) (book2Mbti.getTfType()*1.5);
                JP= (int) (book2Mbti.getJpType()*1.5);
            } else if (Type!=null &&Objects.equals(Type, "NOMAL")) {
                EI= book2Mbti.getEiType();
                SN= book2Mbti.getSnType();
                TF= book2Mbti.getTfType();
                JP= book2Mbti.getJpType();
            }

            if(!childBookDisLikeRepository.existsByChild_ChildIdAndBook_BookId(ChildId, BookId))
            {
                ChildBookDislike childBookDislike = new ChildBookDislike();
                childBookDislike.setChild(child1);
                childBookDislike.setBook(book1);
                childBookDisLikeRepository.save(childBookDislike);
            }
            else
            {
                bookResultDto.setSuccess("duplicate");
                return bookResultDto;
            }
            mbti.setEiType(mbti.getEiType()-EI);
            mbti.setSnType(mbti.getSnType()-SN);
            mbti.setTfType(mbti.getTfType()-TF);
            mbti.setJpType(mbti.getJpType()-JP);
            mbtiRepository.save(mbti);
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
                childBookLike.setDelDate(futureDate);
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

}
