package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookMBTItEST {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ChildRepository childRepository;

    @InjectMocks
    private MbtiRecommendServiceImpl mbtiRecommendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 가중치 계산 로직
    @Test
    void calculateWeightedScore_shouldReturnCorrectScore() {
        // Given
        ChildMbti childMbti = new ChildMbti();
        Mbti mbti = new Mbti();
        mbti.updateMbti(50, -50, 50, -50);
        childMbti.setMbti(mbti);

        BookDto book = new BookDto();
        book.setBookMbti(new MbtiDto(1L,4, -4, 6, -6));

        // When
        int score = mbtiRecommendService.calculateWeightedScore(childMbti, book);
        System.out.println(score);
        // Then
        assertEquals(20, score); // 예상 점수
    }

    // 가중치에 따른 정렬
    @Test
    void recommendBooks_shouldReturnBooksSortedByScore() {
        // Given
        Long childId = 1L;
        Child child = new Child();
        ChildMbti childMbti = new ChildMbti();
        Mbti mbti = new Mbti();
        mbti.updateMbti(50, -50, 50, -50);
        childMbti.setMbti(mbti);
        child.setChildMbti(Collections.singletonList(childMbti));

        when(childRepository.findById(childId)).thenReturn(Optional.of(child));

        BookDto book1 = new BookDto();
        book1.setBookMbti(new MbtiDto(1L, 4, -4, 6, -6));

        BookDto book2 = new BookDto();
        book2.setBookMbti(new MbtiDto(2L, 3, -3, 5, -5));

        when(bookRepository.ChildAgefindAllBookDto(childId)).thenReturn(Arrays.asList(book1, book2));

        // When
        List<BookDto> recommendedBooks = mbtiRecommendService.recommendBooks(childId);

        // Then
        assertEquals(2, recommendedBooks.size());
        assertTrue(recommendedBooks.get(0).getBooktotalfavor().getCount() >= recommendedBooks.get(1).getBooktotalfavor().getCount());
    }

    @Test
    void SNFTRecommendBooks_shouldReturnRecommendedBooksBySNFT() {
        // Given
        Long childId = 1L;
        Child child = new Child();
        ChildMbti childMbti = new ChildMbti();
        Mbti mbti = new Mbti();
        mbti.updateMbti(0, -90, 90, 0);
        childMbti.setMbti(mbti);
        child.setChildMbti(Collections.singletonList(childMbti));

        when(childRepository.findById(childId)).thenReturn(Optional.of(child));

        BookDto bookSN = new BookDto();
        bookSN.setBookMbti(new MbtiDto(1L, 0, -9, 0, 0));

        BookDto bookFT = new BookDto();
        bookFT.setBookMbti(new MbtiDto(2L, 0, 0, -9, 0));

        // When
        when(bookRepository.ChildAgefindAllBookDto(childId)).thenReturn(Arrays.asList(bookSN, bookFT));

        Map<String, Object> result = mbtiRecommendService.SNFTRecommendBooks(childId);

        // Then
        List<BookDto> SNBooks = (List<BookDto>) result.get("SNrecommendedBooks");
        List<BookDto> FTBooks = (List<BookDto>) result.get("FTrecommendedBooks");

        String childSnType = (String) result.get("childSnType");
        String childTfType = (String) result.get("childTfType");

        assertEquals("S", childSnType);
        assertEquals("F", childTfType);
        assertEquals(1, SNBooks.size());
        assertEquals(1, FTBooks.size());
    }
}
