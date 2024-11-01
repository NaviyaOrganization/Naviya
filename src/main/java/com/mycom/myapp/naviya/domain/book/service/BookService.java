package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    BookResultDto delBook(Long bookId);
    BookResultDto detailBook(Long bookId, Long childId);
    BookResultDto listBook();

    Page<BookDto> getAllBooks(int page, int size, String searchType, String keyword);
    Page<BookDto> getAllBooksLoad(int page, int size);
    BookInsertDto updateBook(BookInsertDto bookInsertDto);
    BookInsertDto insertBook(BookInsertDto bookInsertDto);
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
    BookDetailDto adminBookDetail(Long bookId);
    byte[] getImageBytesByBookId(Long bookId);
    BookResultDto addUserRecentBookIfNotExists(Long childId, Long bookId);

}
