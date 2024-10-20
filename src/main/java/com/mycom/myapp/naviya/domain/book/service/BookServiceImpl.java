package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.entity.BookFavorTotal;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookMbtiRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookLikeRepository;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookSerive {

    private final BookRepository bookRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    private final MbtiRepository mbtiRepository;
    private final BookFavorTotalRepository bookFavorTotalRepository;
    private final BookMbtiRepository bookMbtiRepository;

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
        try {bookRepository.deleteById(bookId);
            bookResultDto.setSuccess("success");
        }catch(Exception e) {
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
        }
        return bookResultDto;
    }
    //들어갈때 storgesession에 넣고 들어가면되지 않나하는 생각이 들어
    //솔직히 있어야되는지 의문이지만 혹시 몰라 만들어놓습니다 to 정유선
    //어차피 자세히보는건 굳이 dto쿼리로 안묶어도 될 것 같아서 필요할 것 같으면 바꿔주세용
    @Override
    public BookResultDto detailBook(Long bookId) {
        // BookRepository의 findAll() 메서드를 통해 모든 Book을 가져옴

        BookResultDto bookResultDto = new BookResultDto();
        try {
            Book book =bookRepository.findBybookId(bookId);
            BookDto bookDto =new BookDto();
            bookDto.setBookId(book.getBookId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setBookImage(book.getBookImage());
            bookDto.setPublisher(book.getPublisher());
            bookDto.setSummary(book.getSummary());
            bookDto.setRecommendedAge(book.getRecommendedAge());
            bookDto.setCategory(book.getCategoryCode());
            bookDto.setFullStory(book.getFullStory());
            List<BookDto>tempDto = new ArrayList<>();
            tempDto.add(bookDto);
            bookResultDto.setBooks(tempDto);  // Book 리스트를 BookResultDto에 설정
            bookResultDto.setSuccess("success");
            return bookResultDto;

        } catch (EntityNotFoundException e) {
            bookResultDto.setSuccess("fail");
            System.err.println(e.getMessage());
            return bookResultDto;
        } catch (Exception e) {
            // 다른 예외 처리 로직
            bookResultDto.setSuccess("fail");
            System.err.println("An unexpected error book detail 발생: " + e.getMessage());
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
    public BookResultDto listbookOrderByCreateDate() {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findBookDtoDescCreateDate();
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
            List<BookDto> bookDto = bookRepository.findBooksByChildId(ChildId);
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
    public BookResultDto listBookFavorCount() {
        BookResultDto bookResultDto=new BookResultDto();
        try{
            List<BookDto> bookDto = bookRepository.findBookDescBookFavor();
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
        List<BookDto> bookDto = bookRepository.findBooksByChildIdOrderByCreatedAtDesc(ChildId);
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

}
