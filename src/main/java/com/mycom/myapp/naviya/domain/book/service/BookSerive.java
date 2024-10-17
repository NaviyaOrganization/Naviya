package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;

public interface BookSerive {
    BookResultDto delBook(Long bookId);
    BookResultDto detailBook(BookDto bookDto);
    BookResultDto listBook();
    BookResultDto updateBook(BookDto bookDto);
    BookResultDto insertBook(BookDto bookDto);
    BookResultDto listbookOrderByCreateDate();
    BookResultDto listBookChildFavor(long ChildId);
    BookResultDto listBookFavorCount();
}
