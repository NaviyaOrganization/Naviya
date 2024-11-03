package com.mycom.myapp.naviya.domain.book.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class ChildBookCount {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListBookFavorCount_Success() {
        long childId = 1L;
        List<BookDto> mockBooks = new ArrayList<>();
        mockBooks.add(new BookDto(/* 필드 초기화 */)); // 적절한 필드 초기화

        // Mocking the repository method
        when(bookRepository.findBookDescBookFavorCount(childId)).thenReturn(mockBooks);

        // Act
        BookResultDto result = bookService.listBookFavorCount(childId);

        // Assert
        assertEquals("success", result.getSuccess());
        assertEquals(mockBooks, result.getBooks());
    }

    @Test
    public void testListBookFavorCount_EmptyList() {
        long childId = 1L;

        // Mocking the repository method to return an empty list
        when(bookRepository.findBookDescBookFavorCount(childId)).thenReturn(new ArrayList<>());

        // Act
        BookResultDto result = bookService.listBookFavorCount(childId);

        // Assert
        assertEquals("success", result.getSuccess());
        assertTrue(result.getBooks().isEmpty());
    }

    @Test
    public void testListBookFavorCount_Exception() {
        long childId = 1L;

        // 여기에 Mockito에서 예외를 던지도록 설정할 수 있습니다.
        when(bookRepository.findBookDescBookFavorCount(childId)).thenThrow(new RuntimeException("Database error"));

        // Act
        BookResultDto result = bookService.listBookFavorCount(childId);

        // Assert
        assertEquals("fail", result.getSuccess());
        assertNull(result.getBooks());
    }
}
