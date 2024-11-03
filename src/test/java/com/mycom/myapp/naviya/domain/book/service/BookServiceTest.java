package com.mycom.myapp.naviya.domain.book.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        // 초기화 작업이 필요하다면 여기에 추가
    }

    @Test
    public void testDelBook_Success() {
        Long bookId = 20L;

        BookResultDto resultDto = bookService.delBook(bookId);

        assertEquals("success", resultDto.getSuccess());
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    public void testDelBook_Failure() {
        Long bookId = 20L;

        // deleteById 호출 시 예외를 던지도록 설정
        doThrow(new RuntimeException("Error deleting book")).when(bookRepository).deleteById(bookId);

        BookResultDto resultDto = bookService.delBook(bookId);

        assertEquals("fail", resultDto.getSuccess());
        verify(bookRepository, times(1)).deleteById(bookId);
    }
}
