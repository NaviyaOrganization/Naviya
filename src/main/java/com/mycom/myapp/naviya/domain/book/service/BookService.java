package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;

import java.util.List;

public interface BookService {
    BookResultDto delBook(Long bookId);
    BookResultDto detailBook(Long bookId, Long childId);
    BookResultDto listBook();
    BookResultDto updateBook(BookInsertDto bookInsertDto);
    BookResultDto insertBook(BookInsertDto bookInsertDto);
    BookResultDto listbookOrderByCreateDate(long childId);
    BookResultDto listBookChildFavor(long childId);
    BookResultDto listBookFavorCount(long childId);
    BookResultDto listBookChildRecntRead(long childId);
    BookResultDto ChildBookLike(long bookId, long childId, String type);
    BookResultDto DelChildBookLike(long bookId, long childId);
    BookResultDto DelChildBookDisLike(long bookId, long childId);
    BookResultDto LogicDelChildBookLike(long bookId, long childId);
    BookResultDto ChildBookDisLike(long bookId, long childId, String type);
    List<BookDto> searchBooks(String searchType, String keyword);
    BookResultDto CategoryList(long childId);
    BookResultDto CategoryDisLike(long childId, String category);
    BookResultDto CategoryLike(long childId, String category);
    BookResultDto BookCategoryOne(long childId, String categoryId);
    BookResultDto ChildALLlistBook(Long childId);
    BookResultDto NoCHildlistbookOrderByCreateDate();
    BookResultDto NoChildlistBookChildFavor();

}
