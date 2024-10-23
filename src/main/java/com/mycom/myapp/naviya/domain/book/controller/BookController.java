package com.mycom.myapp.naviya.domain.book.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
//import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Book")
public class BookController {
    private final BookServiceImpl bookServiceImpl;
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

}
