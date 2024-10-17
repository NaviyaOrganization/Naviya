package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.category.dto.CategoryBookDto;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookSerive {

    private final BookRepository bookRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    @Override
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
    public BookResultDto detailBook(BookDto bookDto) {
        return null;
    }

    @Override
    public BookResultDto listBook() {

        // BookRepository의 findAll() 메서드를 통해 모든 Book을 가져옴
        List<Book> books =bookRepository.findAll();
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : books) {
            BookDto bookDto = new BookDto();
            bookDto.setBookId(book.getBookId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setCount(book.getCount());
            CategoryBookDto categoryBookDto = new CategoryBookDto();
            categoryBookDto.setCategoryName(book.getCategory().getCategoryName());
            categoryBookDto.setCategoryId(book.getCategory().getCategoryId());
            bookDto.setCategoryBookDto(categoryBookDto);
            bookDto.setPublisher(book.getPublisher());
            bookDto.setEiType(book.getEiType());
            bookDto.setSnType(book.getSnType());
            bookDto.setTfType(book.getTfType());
            bookDto.setJpType(book.getJpType());
            bookDto.setSummary(book.getSummary());
            bookDto.setRecommendedAge(book.getRecommendedAge());
            bookDtos.add(bookDto);
        }
        // DTO에 가져온 책 목록을 담음
        BookResultDto bookResultDto = new BookResultDto();
        bookResultDto.setBooks(bookDtos);  // Book 리스트를 BookResultDto에 설정
        bookResultDto.setSuccess("success");
        return bookResultDto;
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
    public BookResultDto listbookOrderByCreateDate() {
        List<Book> books =bookRepository.findAllByOrderByCreateDateDesc();
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : books) {
            BookDto bookDto = new BookDto();
            bookDto.setBookId(book.getBookId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setCount(book.getCount());
            CategoryBookDto categoryBookDto = new CategoryBookDto();
            categoryBookDto.setCategoryName(book.getCategory().getCategoryName());
            categoryBookDto.setCategoryId(book.getCategory().getCategoryId());
            bookDto.setCategoryBookDto(categoryBookDto);
            bookDto.setPublisher(book.getPublisher());
            bookDto.setEiType(book.getEiType());
            bookDto.setSnType(book.getSnType());
            bookDto.setTfType(book.getTfType());
            bookDto.setJpType(book.getJpType());
            bookDto.setSummary(book.getSummary());
            bookDto.setCreateDate(book.getCreateDate());
            bookDto.setRecommendedAge(book.getRecommendedAge());
            bookDtos.add(bookDto);
        }
        // DTO에 가져온 책 목록을 담음
        BookResultDto bookResultDto = new BookResultDto();
        bookResultDto.setBooks(bookDtos);  // Book 리스트를 BookResultDto에 설정
        bookResultDto.setSuccess("success");
        return bookResultDto;
    }

    @Override
    public BookResultDto listBookChildFavor(long ChildId) {
        List<Book> books =childBookLikeRepository.findBooksByChildId(ChildId);
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : books) {
            BookDto bookDto = new BookDto();
            bookDto.setBookId(book.getBookId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setCount(book.getCount());
            CategoryBookDto categoryBookDto = new CategoryBookDto();
            categoryBookDto.setCategoryName(book.getCategory().getCategoryName());
            categoryBookDto.setCategoryId(book.getCategory().getCategoryId());
            bookDto.setCategoryBookDto(categoryBookDto);
            bookDto.setPublisher(book.getPublisher());
            bookDto.setEiType(book.getEiType());
            bookDto.setSnType(book.getSnType());
            bookDto.setTfType(book.getTfType());
            bookDto.setJpType(book.getJpType());
            bookDto.setSummary(book.getSummary());
            bookDto.setCreateDate(book.getCreateDate());
            bookDto.setRecommendedAge(book.getRecommendedAge());
            bookDtos.add(bookDto);
        }
        // DTO에 가져온 책 목록을 담음
        BookResultDto bookResultDto = new BookResultDto();
        bookResultDto.setBooks(bookDtos);  // Book 리스트를 BookResultDto에 설정
        bookResultDto.setSuccess("success");
        return bookResultDto;
    }

    @Override
    public BookResultDto listBookFavorCount() {
        List<Book> books =bookRepository.findAllByOrderByCountDesc();
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : books) {
            BookDto bookDto = new BookDto();
            bookDto.setBookId(book.getBookId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setCount(book.getCount());
            CategoryBookDto categoryBookDto = new CategoryBookDto();
            categoryBookDto.setCategoryName(book.getCategory().getCategoryName());
            categoryBookDto.setCategoryId(book.getCategory().getCategoryId());
            bookDto.setCategoryBookDto(categoryBookDto);
            bookDto.setPublisher(book.getPublisher());
            bookDto.setEiType(book.getEiType());
            bookDto.setSnType(book.getSnType());
            bookDto.setTfType(book.getTfType());
            bookDto.setJpType(book.getJpType());
            bookDto.setSummary(book.getSummary());
            bookDto.setCreateDate(book.getCreateDate());
            bookDto.setRecommendedAge(book.getRecommendedAge());
            bookDtos.add(bookDto);
        }
        // DTO에 가져온 책 목록을 담음
        BookResultDto bookResultDto = new BookResultDto();
        bookResultDto.setBooks(bookDtos);  // Book 리스트를 BookResultDto에 설정
        bookResultDto.setSuccess("success");
        return bookResultDto;
    }
}
