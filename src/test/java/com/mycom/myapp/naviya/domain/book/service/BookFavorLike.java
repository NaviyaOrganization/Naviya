package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.entity.BookFavorTotal;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookFavorLike {

    @Autowired
    private BookFavorTotalRepository bookFavorTotalRepository;

    @Transactional
    public void likeBook(Long bookId) {
        BookFavorTotal bookFavorTotal = bookFavorTotalRepository.findByBook_BookId(bookId);
        bookFavorTotal.setCount(bookFavorTotal.getCount() + 1);
        bookFavorTotalRepository.save(bookFavorTotal);
    }

    @Transactional
    public void dislikeBook(Long bookId) {
        BookFavorTotal bookFavorTotal = bookFavorTotalRepository.findByBook_BookId(bookId);
        bookFavorTotal.setCount(bookFavorTotal.getCount() - 1);
        bookFavorTotalRepository.save(bookFavorTotal);
    }

    public BookFavorTotal getBookFavorTotal(Long bookId) {
        return bookFavorTotalRepository.findByBook_BookId(bookId);

    }
}
