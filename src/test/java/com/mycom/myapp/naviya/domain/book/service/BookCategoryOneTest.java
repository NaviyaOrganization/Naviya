package com.mycom.myapp.naviya.domain.book.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스 사용
class BookCategoryOneTest {

    @Autowired
    private BookServiceImpl yourService; // 테스트할 서비스 클래스

    @Test
    void testBookCategoryOne_Success() {
        // Given
        long childId = 1L; // 데이터베이스에 존재하는 childId
        String categoryId = "001"; // 데이터베이스에 존재하는 categoryId

        // When
        BookResultDto result = yourService.BookCategoryOne(childId, categoryId);

        // Then
        assertEquals("success", result.getSuccess());
        assertNotNull(result.getBooks());
    }
    @Test
    void testBookCategoryOne_Fail() {
        // Given
        long childId = 1L; // 데이터베이스에 존재하는 childId
        String categoryId = "000"; // 데이터베이스에 없는 categoryId

        // When
        BookResultDto result = yourService.BookCategoryOne(childId, categoryId);

        // Then
        assertEquals("success", result.getSuccess());

    }
}