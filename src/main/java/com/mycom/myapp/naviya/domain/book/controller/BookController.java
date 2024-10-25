package com.mycom.myapp.naviya.domain.book.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
//import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import com.mycom.myapp.naviya.domain.book.service.MbtiRecommendServiceImpl;
import com.mycom.myapp.naviya.global.response.ResponseForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.mycom.myapp.naviya.global.response.ResponseCode.EXAMPLE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Book")
public class BookController {
    private final BookServiceImpl bookServiceImpl;
    private final MbtiRecommendServiceImpl mbtiRecommendService;
    @GetMapping("/List")
    public BookResultDto AllBookList()
    {
        return bookServiceImpl.listBook();
    }
    @GetMapping("/ListOrderDate")
    public BookResultDto ListOrderDate(@RequestParam  long childId)
    {
        return bookServiceImpl.listbookOrderByCreateDate(childId);
    }
    @GetMapping("/ListBookFavorCount")
    public BookResultDto ListBookFavorCount(@RequestParam  long childId)
    {
        return bookServiceImpl.listBookFavorCount(childId);
    }
    @GetMapping("/ListBookChildFavor")
    public BookResultDto ListBookChildFavor(@RequestParam long childId)
    {
        return bookServiceImpl.listBookChildFavor(childId);
    }
    @GetMapping("/ListBookChildRecntRead")
    public BookResultDto ListBookChildRecntRead(@RequestParam long childId)
    {
        return bookServiceImpl.listBookChildRecntRead(childId);
    }
    @DeleteMapping("/BookDel")
    public BookResultDto DeleteBook(@RequestParam long bookId)
    {
        return bookServiceImpl.delBook(bookId);
    }
    @GetMapping("/BookDetail")
    public BookResultDto DetailBook(@RequestParam long bookId ,@RequestParam long childId)
    {
        return bookServiceImpl.detailBook(bookId,childId);
    }
    @GetMapping("/BookLike")
    public BookResultDto BookLike(@RequestParam long bookId,@RequestParam long childId,String Type)
    {
        return bookServiceImpl.ChildBookLike(bookId,childId,Type);
    }
    @GetMapping("/BookDisLike")
    public BookResultDto BookDisLike(@RequestParam long bookId,@RequestParam long childId , String Type)
    {
        return bookServiceImpl.ChildBookDisLike(bookId,childId,Type);
    }
    @DeleteMapping("/DelBookLike")
    public BookResultDto DelBookLike(@RequestParam long bookId,@RequestParam long childId)
    {
        return bookServiceImpl.DelChildBookLike(bookId,childId);
    }
    //얘가 현재 deletedate 저장하는 하는애
    @DeleteMapping("/DelBookLikeLogical")
    public BookResultDto DelBookLikeLogical(@RequestParam long bookId,@RequestParam long childId)
    {
        return bookServiceImpl.LogicDelChildBookLike(bookId,childId);
    }
    @DeleteMapping("/DelBookDisLike")
    public BookResultDto DelBookDisLike(@RequestParam long bookId,@RequestParam long childId)
    {
        return bookServiceImpl.DelChildBookDisLike(bookId,childId);
    }

    @GetMapping("/recommend")
    public ResponseForm recommendBook(@RequestParam Long childId) {
        List<BookDto> books = mbtiRecommendService.recommendBooks(childId);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }

    @GetMapping("/snftrecommend")
    public ResponseForm SNFTRecommendBooks(@RequestParam Long childId) {
        Map<String, Object> books = mbtiRecommendService.SNFTRecommendBooks(childId);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }

    @GetMapping("/search")
    public ResponseForm searchBooks(@RequestParam String searchType, String keyword) {
        List<BookDto> books = bookServiceImpl.searchBooks(searchType, keyword);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }
    //카테고리 리스트
    @GetMapping("/CategoryList")
    public BookResultDto BookCategoryLike(@RequestParam long childId) {
        return bookServiceImpl.CategoryList(childId);
    }
    //카테고리 좋아요
    @GetMapping("/CategoryDisLike")
    public BookResultDto BookCategoryDisLike(@RequestParam long childId,@RequestParam String Ctegory) {
        return bookServiceImpl.CategoryDisLike(childId,Ctegory);
    }
    //카테고리 싫어요
    @GetMapping("/CategoryLike")
    public BookResultDto BookCategoryLike(@RequestParam long childId,@RequestParam String Ctegory) {
        return bookServiceImpl.CategoryLike(childId,Ctegory);
    }


}
