package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookMbtiRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto;
import com.mycom.myapp.naviya.domain.child.entity.*;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.constant.ConstantDescs.NULL;

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
    private final ChildFavorCategoryRepository childFavorCategoryRepository;
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
            //bookFavorTotalRepository.incrementCountIfNotLiked(ChildId, BookId);

            //원본
            //상대편에 싫어요 있으면 빼주기
            childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(ChildId, BookId);
            //일대다여서 확인해야함
            //좋아요 토탈 카운트 올려주기 && 이미 deldate없는 좋아요는 올려주지 않기
            if(!childBookLikeRepository.existsByChildIdAndBookIdAndDelDateIsNull(ChildId, BookId)) {
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
            childBookLikeRepository.save(childBookLike); // ChildBookLike만 명시적으로 저장

            // mbti는 이미 조회된 상태에서 변경되었으므로, save 없이도 트랜잭션이 끝나면 자동 반영
            mbti.setEiType(EI);
            mbti.setSnType(SN);
            mbti.setTfType(TF);
            mbti.setJpType(JP);

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

}
