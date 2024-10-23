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
    public BookResultDto DetailBook(@RequestParam long bookId)
    {
        return bookServiceImpl.detailBook(bookId);
    }
    @GetMapping("/BookLike")
    public BookResultDto BookLike(@RequestParam long bookId,@RequestParam long childId,@RequestParam String Type)
    {
        return bookServiceImpl.ChildBookLike(bookId,childId,Type);
    }
    @GetMapping("/BookDisLike")
    public BookResultDto BookDisLike(@RequestParam long bookId,@RequestParam long childId ,@RequestParam String Type)
    {
        return bookServiceImpl.ChildBookDisLike(bookId,childId,Type);
    }
    @DeleteMapping("/DelBookLike")
    public BookResultDto DelBookLike(@RequestParam long bookId,@RequestParam long childId)
    {
        return bookServiceImpl.DelChildBookLike(bookId,childId);
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

    @GetMapping("/search")
    public ResponseForm searchBooks(@RequestParam String searchType, String keyword) {
        List<BookDto> books = bookServiceImpl.searchBooks(searchType, keyword);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }
}
