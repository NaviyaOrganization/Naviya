package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;

import java.util.List;

public interface BookSerive {
    BookResultDto delBook(Long bookId);
    BookResultDto detailBook(Long bookId,Long childId);
    BookResultDto listBook();
    BookResultDto updateBook(BookDto bookDto);
    BookResultDto insertBook(BookDto bookDto);
    BookResultDto listbookOrderByCreateDate(long childId);
    BookResultDto listBookChildFavor(long ChildId);
    BookResultDto listBookFavorCount(long childId);
    BookResultDto listBookChildRecntRead(long ChildId);
    BookResultDto ChildBookLike(long BookId, long ChildId,String Type);
    BookResultDto DelChildBookLike(long BookId, long ChildId);
    BookResultDto DelChildBookDisLike(long BookId, long ChildId);
    BookResultDto LogicDelChildBookLike(long BookId, long ChildId);
    BookResultDto ChildBookDisLike(long BookId, long ChildId,String Type);
    List<BookDto> searchBooks(String searchType, String keyword);

}
