package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.dto.*;
import com.mycom.myapp.naviya.domain.book.entity.Book;
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
import com.mycom.myapp.naviya.domain.common.entity.Code;
import com.mycom.myapp.naviya.domain.common.repository.CodeRepository;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto;
import com.mycom.myapp.naviya.domain.child.entity.*;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final LikeDislikeProcessor likeDislikeProcessor;
    private final CodeRepository codeRepository;



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
    public BookResultDto detailBook(Long bookId, Long childId) {
        BookResultDto bookResultDto = new BookResultDto();
        BookDetailDto bookDetailDto = new BookDetailDto();
        try {
            Book book = bookRepository.findBybookId(bookId);

            // Book 기본 정보 설정
            bookDetailDto.setBookId(book.getBookId());
            bookDetailDto.setTitle(book.getTitle());
            bookDetailDto.setSummary(book.getSummary());
            bookDetailDto.setRecommendedAge(book.getRecommendedAge());
            bookDetailDto.setPublisher(book.getPublisher());
            bookDetailDto.setAuthor(book.getAuthor());
            bookDetailDto.setCreatedAt(book.getCreatedAt());
            bookDetailDto.setFullStory(book.getFullStory());

            // BookImage를 그대로 설정
            if (book.getBookImage() != null) {
                // String을 byte[]로 변환 후 Base64로 인코딩
                byte[] imageBytes = book.getBookImage().getBytes(StandardCharsets.UTF_8);
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                bookDetailDto.setBookImage(base64Image);
            }

            // Code 엔티티에서 codeName 조회 후 매핑
            codeRepository.findByCode(book.getCategoryCode())
                    .ifPresentOrElse(
                            code -> bookDetailDto.setCategory(code.getCodeName()),
                            () -> bookDetailDto.setCategory("Unknown Category")
                    );

            // Mbti 정보가 존재할 경우 매핑
            if (book.getBookMbti() != null) {
                MbtiDto mbtiDto = new MbtiDto();
                mbtiDto.setEiType(book.getBookMbti().getMbti().getEiType());
                mbtiDto.setSnType(book.getBookMbti().getMbti().getSnType());
                mbtiDto.setTfType(book.getBookMbti().getMbti().getTfType());
                mbtiDto.setJpType(book.getBookMbti().getMbti().getJpType());
                bookDetailDto.setBookMbti(mbtiDto);
            }

            bookResultDto.setBookDetail(bookDetailDto);
            bookResultDto.setSuccess("success");
        } catch (EntityNotFoundException e) {
            bookResultDto.setSuccess("fail");
            System.err.println("Book not found with ID: " + bookId + ". " + e.getMessage());
        }

        return bookResultDto;
    }




    //to 유성 일단 서비스에서는 mbti 값 그대로 내리고 프론트  or 컨트롤러에서 추가 구현 및 조정 생각하고 구현해놨음
    //추후 구현방식 협의 후 수정 가능
    //일대일 관계에서는 lazy를 걸어도 eager로 적용되어
    //n+1 이슈로 밀고 dto쿼리로 바꿨음
    @Override
    public BookResultDto listBook() {
        BookResultDto bookResultDto = new BookResultDto();
        try {
            // 모든 Book을 BookDto 리스트로 조회
            List<BookDto> bookDtos = bookRepository.findAllBookDto();

            // 각 BookDto에 대해 category code로 code_name을 조회하여 설정
            for (BookDto bookDto : bookDtos) {
                String categoryCode = bookDto.getCategory();

                // category code를 이용해 Code 엔티티 조회
                Optional<Code> codeOptional = codeRepository.findByCode(categoryCode);

                // code가 존재하면 code_name을, 없으면 "Unknown"을 설정
                String codeName = codeOptional.map(Code::getCodeName).orElse("Unknown");
                bookDto.setCategory(codeName);
            }

            // 설정된 BookDto 리스트를 bookResultDto에 추가하고 성공 메시지 설정
            bookResultDto.setBooks(bookDtos);
            bookResultDto.setSuccess("success");



        } catch (Exception e) {
            e.printStackTrace();
            bookResultDto.setSuccess("fail");
        }
        return bookResultDto;
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
    public BookInsertDto insertBook(BookInsertDto bookInsertDto) {
        // Create and populate the Book entity
        Book book = new Book();
        book.setTitle(bookInsertDto.getTitle());
        book.setSummary(bookInsertDto.getSummary());
        book.setRecommendedAge(bookInsertDto.getRecommendedAge());
        book.setPublisher(bookInsertDto.getPublisher());
        book.setAuthor(bookInsertDto.getAuthor());
        book.setFullStory(bookInsertDto.getFullStory());
        book.setCreatedAt(LocalDateTime.now(ZoneId.systemDefault()));
        book.setBookImage(bookInsertDto.getBookImage());

        // Map category to specific code values
        String categoryCode;
        switch (bookInsertDto.getCategory()) {
            case "소설":
                categoryCode = "010";
                break;
            case "전래동화":
                categoryCode = "020";
                break;
            case "과학":
                categoryCode = "030";
                break;
            case "수학":
                categoryCode = "040";
                break;
            case "역사":
                categoryCode = "050";
                break;
            case "동물":
                categoryCode = "060";
                break;
            case "문학":
                categoryCode = "070";
                break;
            case "자연":
                categoryCode = "080";
                break;
            default:
                categoryCode = "010"; // or handle unknown category as needed
                break;
        }
        book.setCategoryCode(categoryCode);

        // Create and populate the Mbti entity based on dto fields
        Mbti mbti = new Mbti();
        mbti.setEiType(bookInsertDto.getEiType());
        mbti.setSnType(bookInsertDto.getSnType());
        mbti.setTfType(bookInsertDto.getTfType());
        mbti.setJpType(bookInsertDto.getJpType());

        // Create the BookMbti entity and set relations
        BookMbti bookMbti = new BookMbti();
        bookMbti.setBook(book);  // Book relationship
        bookMbti.setMbti(mbti);  // Mbti relationship

        // Set Book's relationship with BookMbti and persist
        book.setBookMbti(bookMbti);
        bookRepository.save(book);

        return bookInsertDto;
    }



    @Override
    public BookInsertDto updateBook(BookInsertDto bookInsertDto) {
        Book book = bookRepository.findById(bookInsertDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookInsertDto.getBookId()));

        // Null 체크를 포함한 필드 업데이트
        if (bookInsertDto.getTitle() != null) book.setTitle(bookInsertDto.getTitle());
        if (bookInsertDto.getSummary() != null) book.setSummary(bookInsertDto.getSummary());
        if (bookInsertDto.getRecommendedAge() != null) book.setRecommendedAge(bookInsertDto.getRecommendedAge());
        if (bookInsertDto.getPublisher() != null) book.setPublisher(bookInsertDto.getPublisher());
        if (bookInsertDto.getAuthor() != null) book.setAuthor(bookInsertDto.getAuthor());
        if (bookInsertDto.getCategory() != null) book.setCategoryCode(bookInsertDto.getCategory());

        // MBTI 업데이트
        BookMbti bookMbti = book.getBookMbti();
        if (bookMbti == null) {
            bookMbti = new BookMbti();
            bookMbti.setBook(book);
            book.setBookMbti(bookMbti);
        }

        Mbti mbti = bookMbti.getMbti();
        if (mbti == null) {
            mbti = new Mbti();
            bookMbti.setMbti(mbti);
        }

        if (bookInsertDto.getEiType() != null) mbti.setEiType(bookInsertDto.getEiType());
        if (bookInsertDto.getSnType() != null) mbti.setSnType(bookInsertDto.getSnType());
        if (bookInsertDto.getTfType() != null) mbti.setTfType(bookInsertDto.getTfType());
        if (bookInsertDto.getJpType() != null) mbti.setJpType(bookInsertDto.getJpType());


        bookRepository.save(book);

        return bookInsertDto;
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
    public BookDetailDto adminBookDetail(Long bookId) {
        Book book = bookRepository.findBybookId(bookId);
        BookDetailDto bookDetailDto = new BookDetailDto();
        bookDetailDto.setBookId(book.getBookId());
        bookDetailDto.setTitle(book.getTitle());
        bookDetailDto.setSummary(book.getSummary());
        bookDetailDto.setRecommendedAge(book.getRecommendedAge());
        bookDetailDto.setPublisher(book.getPublisher());
        bookDetailDto.setAuthor(book.getAuthor());
        bookDetailDto.setCreatedAt(book.getCreatedAt());
        bookDetailDto.setFullStory(book.getFullStory());
        bookDetailDto.setBookImage(book.getBookImage());
        bookDetailDto.setCategory(book.getCategoryCode());

        // Map the MBTI if it exists
        Optional.ofNullable(book.getBookMbti()).ifPresent(bookMbti -> {
            MbtiDto mbtiDto = new MbtiDto(
                    bookMbti.getMbti().getMbtiId(),
                    bookMbti.getMbti().getEiType(),
                    bookMbti.getMbti().getSnType(),
                    bookMbti.getMbti().getTfType(),
                    bookMbti.getMbti().getJpType()
            );
            bookDetailDto.setBookMbti(mbtiDto);
        });
        return bookDetailDto;
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


            List<ChildMbti> childMbtis= child1.getChildMbti();
            ChildMbti childMbti_val =new ChildMbti();
            int flag=0;

            for( ChildMbti childMbti_Temp : childMbtis)
            {
                if(childMbti_Temp.getDeletedAt()==null)
                {
                    childMbti_val=childMbti_Temp;
                    flag=1;
                    break;
                }
            }
            if (flag==0) {
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
            Mbti book2Mbti=bookMbti.getMbti();
            Mbti mbti=childMbti_val.getMbti();

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
            childBookLikeRepository.save(childBookLike); // ChildBookLike만 명시적으로 저장

            // mbti는 이미 조회된 상태에서 변경되었으므로, save 없이도 트랜잭션이 끝나면 자동 반영
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
            List<ChildMbti> childMbtis= child1.getChildMbti();
            ChildMbti childMbti_val =new ChildMbti();
            int flag=0;

            for( ChildMbti childMbti_Temp : childMbtis)
            {
                if(childMbti_Temp.getDeletedAt()==null)
                {
                    childMbti_val=childMbti_Temp;
                    flag=1;
                    break;
                }
            }
            if (flag==0) {
                bookResultDto.setSuccess("fail3");
                return bookResultDto;
            }
            Mbti mbti = childMbti_val.getMbti();
            childBookLikeRepository.deleteByChildIdAndBookIdAndDelDateIsNull(ChildId, BookId);
            //상대편 토탈 좋아요 카운트 내려야함
            bookFavorTotalRepository.decrementCountByBookId(BookId);
            BookMbti bookMbti = book1.getBookMbti();
            Mbti book2Mbti=bookMbti.getMbti();

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
            List<BookDto> bookDto = bookRepository.findBookDtoOneCateogory(childId,categoryId);
            bookResultDto.setBooks(bookDto);  // Book 리스트를 BookResultDto에 설정
            System.out.println(bookResultDto);
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
    public byte[] getImageBytesByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        // Assuming book.getBookImage() returns a Base64 encoded String
        String base64Image = book.getBookImage();

        // Decode Base64 String to byte[]
        return Base64.getDecoder().decode(base64Image);
    }


}
