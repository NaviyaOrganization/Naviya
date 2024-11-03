package com.mycom.myapp.naviya.domain.book.service;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto;
import com.mycom.myapp.naviya.domain.child.repository.ChildFavorCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryBookListTest {

    @InjectMocks
    private BookServiceImpl yourService; // 테스트할 서비스

    @Mock
    private BookRepository bookRepository; // BookRepository Mock
    @Mock
    private ChildFavorCategoryRepository childFavorCategoryRepository; // ChildFavorCategoryRepository Mock

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    void testCategoryList_WithZeroChildId_ReturnsAllBooks() {
        // Given
        List<BookDto> allBooks = Arrays.asList(new BookDto(), new BookDto(), new BookDto());
        when(bookRepository.findAllBookDto()).thenReturn(allBooks);

        // When
        BookResultDto result = yourService.CategoryList(0L);

        // Then
        assertEquals("success", result.getSuccess());
        assertEquals(allBooks, result.getBooks());
    }

    @Test
    void testCategoryList_WithValidChildId_ReturnsBooksBasedOnWeight() {
        // Given
        long childId = 1L;
        String categoryCode1 = "030";
        String categoryCode2 = "031";

        ChildFavCategoryDto category1 = new ChildFavCategoryDto();
        category1.setCategoryCode(categoryCode1);
        category1.setChildFavorCategoryWeight(70L); // 가중치 70

        ChildFavCategoryDto category2 = new ChildFavCategoryDto();
        category2.setCategoryCode(categoryCode2);
        category2.setChildFavorCategoryWeight(30L); // 가중치 30

        when(childFavorCategoryRepository.findFavCategoriesByChildIdDeletedAtIsNull(childId))
                .thenReturn(Arrays.asList(category1, category2));

        BookDto book1 = new BookDto();
        book1.setCategory(categoryCode1);
        BookDto book2 = new BookDto();
        book2.setCategory(categoryCode2);
        BookDto book3 = new BookDto();
        book3.setCategory(categoryCode1);
        when(bookRepository.findAllBookDtoByCategoryCodes(Arrays.asList(categoryCode1, categoryCode2), childId))
                .thenReturn(Arrays.asList(book1, book2, book3));

        // When
        BookResultDto result = yourService.CategoryList(childId);

        // Then
        assertEquals("success", result.getSuccess());
        assertEquals(3, result.getBooks().size()); // 책 개수는 비율에 따라 결정
        // 추가적으로 반환된 책의 카테고리를 확인할 수 있습니다.
        assertTrue(result.getBooks().stream().anyMatch(book -> book.getCategory().equals(categoryCode1)));
        assertTrue(result.getBooks().stream().anyMatch(book -> book.getCategory().equals(categoryCode2)));
    }

    @Test
    void testCategoryList_ExceptionHandling() {
        // Given
        long childId = 2L;

        // When
        BookResultDto result = yourService.CategoryList(childId);

        // Then
        assertEquals("success", result.getSuccess());
    }
}

