package com.mycom.myapp.naviya.domain.book.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
