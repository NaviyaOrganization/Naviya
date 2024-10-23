package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MbtiRecommendServiceImpl implements MbtiRecommendService {

    private final BookRepository bookRepository;
    private final ChildRepository childRepository;

    public int calculateWeightedScore(Child child, BookDto book) {
        int score = 0;
        int[] childMbti = new int[4];

        if (child.getChildMbti() != null) {
            childMbti[0] = child.getChildMbti().getMbti().getEiType();
            childMbti[1] = child.getChildMbti().getMbti().getSnType();
            childMbti[2] = child.getChildMbti().getMbti().getTfType();
            childMbti[3] = child.getChildMbti().getMbti().getJpType();
        } else {
            childMbti[0] = 0;
            childMbti[1] = 0;
            childMbti[2] = 0;
            childMbti[3] = 0;
        }

        int[] bookMbti = {
                book.getBookMbti().getEiType(),
                book.getBookMbti().getSnType(),
                book.getBookMbti().getTfType(),
                book.getBookMbti().getJpType()
        };

        // 각 MBTI 요소별로 가중치 계산 ( 같을 때, 차이가 3 이내, 5 이내, 7 이내인 경우)
        for (int i = 0; i < 4; i++) {
            int diff = Math.abs(childMbti[i] - bookMbti[i]); // 차이 계산

            if (diff == 0) {
                score += 5; // 완전히 같을 때
            } else if (diff <= 3) {
                score += 3; // 차이가 3 이내일 때
            } else if (diff <= 5) {
                score += 2; // 차이가 5 이내일 때
            } else if (diff <= 7) {
                score += 1; // 차이가 7 이내일 때
            }

        }
        return score;
    }

    // MBTI 책 추천 가중치 큰 순서대로 정렬
    // 책의 데이터나 유저 데이터
    // 메인페이지에서 뿌려지는게 맞나 ???
    // mbti만 쓰기에는 너무 단순하다.

//    @Transactional(readOnly = true)
//    @Cacheable(value = "recommendedBooks", key = "#childId")
    public List<BookDto> recommendBooks(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException());
        List<BookDto> books = bookRepository.findAllBookDto();  // 좋아요, 싫어요, 자녀 연령 필터링해서 가져오기

        // 책 리스트를 유사성 점수 차이에 따라 정렬
        Collections.sort(books, new Comparator<BookDto>() {
            @Override
            public int compare(BookDto b1, BookDto b2) {
                int score1 = calculateWeightedScore(child, b1);
                int score2 = calculateWeightedScore(child, b2);
                return Integer.compare(score2, score1); // 점수가 큰 순서대로 정렬
            }
        });

        for (BookDto bookDto : books) {
            int weightScore = calculateWeightedScore(child, bookDto);
            bookDto.setCategory(String.valueOf(weightScore)); // 임의 설정

        }

        return books;
    }

}