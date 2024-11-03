package com.mycom.myapp.naviya.domain.book.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookDisLikeRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ChildBookLIkeDisLikeTest {

    @Mock
    private ChildBookLikeRepository childBookLikeRepository;
    @Mock
    ChildBookDisLikeRepository childBookDisLikeRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDelChildBookLike_Success() {
        long bookId = 1L;
        long childId = 1L;

        // Act
        BookResultDto result = bookService.DelChildBookLike(bookId, childId);

        // Assert
        verify(childBookLikeRepository).deleteByChildIdAndBookId(childId, bookId);
        assertEquals("success", result.getSuccess());
    }

    @Test
    public void testDelChildBookLike_NotFound() {
        long bookId = 1L;
        long childId = 1L;

        // 여기에 Mockito에서 예외를 던지도록 설정할 수 있습니다.
        doThrow(new RuntimeException("Entity not found")).when(childBookLikeRepository).deleteByChildIdAndBookId(childId, bookId);

        // Act
        BookResultDto result = bookService.DelChildBookLike(bookId, childId);

        // Assert
        assertEquals("fail", result.getSuccess());
    }

    @Test
    public void testDelChildBookLike_Exception() {
        long bookId = 1L;
        long childId = 1L;

        // 여기에 Mockito에서 예외를 던지도록 설정할 수 있습니다.
        doThrow(new RuntimeException("Some error")).when(childBookLikeRepository).deleteByChildIdAndBookId(childId, bookId);

        // Act
        BookResultDto result = bookService.DelChildBookLike(bookId, childId);

        // Assert
        assertEquals("fail", result.getSuccess());
    }


    @Test
    public void testDelChildBookDisLike_NotFound() {
        long bookId = 1L;
        long childId = 1L;

        // 여기에 Mockito에서 예외를 던지도록 설정할 수 있습니다.
        doThrow(new RuntimeException("Entity not found")).when(childBookDisLikeRepository).deleteByChild_ChildIdAndBook_BookId(childId, bookId);

        // Act
        BookResultDto result = bookService.DelChildBookDisLike(bookId, childId);

        // Assert
        assertEquals("fail", result.getSuccess());
    }

    @Test
    public void testDelChildBookDisLike_Exception() {
        long bookId = 1L;
        long childId = 1L;

        // 여기에 Mockito에서 예외를 던지도록 설정할 수 있습니다.
        doThrow(new RuntimeException("Some error")).when(childBookDisLikeRepository).deleteByChild_ChildIdAndBook_BookId(childId, bookId);

        // Act
        BookResultDto result = bookService.DelChildBookDisLike(bookId, childId);

        // Assert
        assertEquals("fail", result.getSuccess());
    }
}