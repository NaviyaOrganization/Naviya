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
    public int weightCal(int calWeight,int weight,int sign)
    {
        Double baseAdjustment = (double) calWeight;
        Double currentWeight = (double) weight;
        //절대값이라 현재 mbti 가중치가 음수여도 상관 없음
        int dynamicAdjustment = (int) (baseAdjustment * (1.2 - Math.abs(currentWeight / 100)));
        int newWeight = Math.max(-100, Math.min(100, (int)(currentWeight + (dynamicAdjustment*sign))));
        return newWeight;
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
                bookResultDto.setSuccess("fail1");
                return bookResultDto;
            }
            Optional book=bookRepository.findById(BookId);
            Book book1=new Book();
            if(book.isPresent()){
                book1=(Book)book.get();
            }
            else
            {
                bookResultDto.setSuccess("fail2");
                return bookResultDto;
            }
            ChildMbti childMbti = child1.getChildMbti();
            if (childMbti == null) {
                bookResultDto.setSuccess("fail3");
                return bookResultDto;
            }
            //상대편에 싫어요 있으면 빼주기
            childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(ChildId, BookId);
            //일대다여서 확인해야함
            //좋아요 토탈 카운트 올려주기 && 이미 deldate없는 좋아요는 올려주지 않기
            if(!childBookLikeRepository.existsByChildIdAndBookIdAndDelDateIsNull(ChildId, BookId))
            {
                bookFavorTotalRepository.incrementCountByBookId(BookId);
            }
            else
            {
                bookResultDto.setSuccess("duplicate");
                return bookResultDto;
                //이미 있음
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
                EI =weightCal(book2Mbti.getEiType(),mbti.getEiType(),1);
                SN= weightCal(book2Mbti.getSnType(),mbti.getSnType(),1);
                TF= weightCal(book2Mbti.getTfType(),mbti.getTfType(),1);
                JP=weightCal(book2Mbti.getJpType(),mbti.getJpType(),1);
            } else if (Type!=null &&Objects.equals(Type, "REVERSE")) {
                EI =weightCal((int)(book2Mbti.getEiType()*1.2),mbti.getEiType(),1);
                SN= weightCal((int)(book2Mbti.getSnType()*1.2),mbti.getSnType(),1);
                TF= weightCal((int)(book2Mbti.getTfType()*1.2),mbti.getTfType(),1);
                JP=weightCal((int)(book2Mbti.getJpType()*1.2),mbti.getJpType(),1);
            }
            else if (Type!=null &&Objects.equals(Type, "NOMAL")) {
                EI =weightCal((int)(book2Mbti.getEiType()*0.7),mbti.getEiType(),1);
                SN= weightCal((int)(book2Mbti.getSnType()*0.7),mbti.getSnType(),1);
                TF= weightCal((int)(book2Mbti.getTfType()*0.7),mbti.getTfType(),1);
                JP=weightCal((int)(book2Mbti.getJpType()*0.7),mbti.getJpType(),1);
            }
            else
            {
                bookResultDto.setSuccess("fail5");
                return bookResultDto;
            }

            ChildBookLike childBookLike = new ChildBookLike();
            childBookLike.setChild(child1);
            childBookLike.setBook(book1);
            childBookLike.setDeletedAt(null);
            childBookLikeRepository.save(childBookLike);

            mbti.setEiType(EI);
            mbti.setSnType(SN);
            mbti.setTfType(TF);
            mbti.setJpType(JP);
            mbtiRepository.save(mbti);
            bookResultDto.setSuccess("success");
            return bookResultDto;
        }
        catch(Exception e){
            e.printStackTrace();
            bookResultDto.setSuccess("fail4");
            return bookResultDto;
        }
    }
    @Override
    @Transactional
    /*
    모두 동적 조정법 적용
*     <a> mbti 별 추천은 그냥 현재 가중치로 한다.
    <b> 일반책은 0.7곱해서 더해준다
    <c>mbti 반대별 좋아요는 의미가 있는거니 1.2 곱해서 더해준다.*/
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
                bookResultDto.setSuccess("fail1");
                return bookResultDto;
            }
            Optional book=bookRepository.findById(BookId);
            Book book1=new Book();
            if(book.isPresent()){
                book1=(Book)book.get();
            }
            else
            {
                bookResultDto.setSuccess("fail2");
                return bookResultDto;
            }
            ChildMbti childMbti = child1.getChildMbti();
            if (childMbti == null) {
                bookResultDto.setSuccess("fail3");
                return bookResultDto;
            }
            //상대편에 좋아요 있으면 빼주기
            childBookLikeRepository.deleteByChildIdAndBookIdAndDelDateIsNull(ChildId, BookId);
            //상대편 토탈 좋아요 카운트 내려야함
            bookFavorTotalRepository.decrementCountByBookId(BookId);
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
                EI =weightCal(book2Mbti.getEiType(),mbti.getEiType(),-1);
                SN= weightCal(book2Mbti.getSnType(),mbti.getSnType(),-1);
                TF= weightCal(book2Mbti.getTfType(),mbti.getTfType(),-1);
                JP=weightCal(book2Mbti.getJpType(),mbti.getJpType(),-1);
            } else if (Type!=null &&Objects.equals(Type, "REVERSE")) {
                EI =weightCal((int)(book2Mbti.getEiType()*1.2),mbti.getEiType(),-1);
                SN= weightCal((int)(book2Mbti.getSnType()*1.2),mbti.getSnType(),-1);
                TF= weightCal((int)(book2Mbti.getTfType()*1.2),mbti.getTfType(),-1);
                JP=weightCal((int)(book2Mbti.getJpType()*1.2),mbti.getJpType(),-1);
            }
            else if (Type!=null &&Objects.equals(Type, "NOMAL")) {
                EI =weightCal((int)(book2Mbti.getEiType()*0.7),mbti.getEiType(),-1);
                SN= weightCal((int)(book2Mbti.getSnType()*0.7),mbti.getSnType(),-1);
                TF= weightCal((int)(book2Mbti.getTfType()*0.7),mbti.getTfType(),-1);
                JP=weightCal((int)(book2Mbti.getJpType()*0.7),mbti.getJpType(),-1);
            }
            else
            {
                bookResultDto.setSuccess("fail5");
                return bookResultDto;
            }

            ChildBookDislike childBookDislike = new ChildBookDislike();
            childBookDislike.setChild(child1);
            childBookDislike.setBook(book1);
            childBookDisLikeRepository.save(childBookDislike);

            mbti.setEiType(EI);
            mbti.setSnType(SN);
            mbti.setTfType(TF);
            mbti.setJpType(JP);
            mbtiRepository.save(mbti);
            bookResultDto.setSuccess("success");
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
}
