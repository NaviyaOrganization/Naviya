package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookFavorTotalDto;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MbtiRecommendServiceImpl implements MbtiRecommendService {

    private final BookRepository bookRepository;
    private final ChildRepository childRepository;

    //나중에 예외처리 빠꾸 예외처리 해줘야함
    public int calculateWeightedScore(Child child, BookDto book) {
        int score = 0;
        int[] childMbti = new int[4];
        List<ChildMbti> childMbtis= child.getChildMbti();
        ChildMbti childMbti_val =new ChildMbti();
        for( ChildMbti childMbti_Temp : childMbtis)
        {
            if(childMbti_Temp.getDeletedAt()==null)
            {
                childMbti_val=childMbti_Temp;
                break;
            }
        }


        if (child.getChildMbti() != null) {
            childMbti[0] = childMbti_val.getMbti().getEiType();
            childMbti[1] = childMbti_val.getMbti().getSnType();
            childMbti[2] =childMbti_val.getMbti().getTfType();
            childMbti[3] =childMbti_val.getMbti().getJpType();
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
                .orElseThrow(() -> new IllegalArgumentException("자식을 찾을 수 없습니다."));
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
            bookDto.setBooktotalfavor(new BookFavorTotalDto((long) weightScore)); // 임의 설정
        }

        return books;
    }

    public Map<String,Object> SNFTRecommendBooks(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자식을 찾을 수 없습니다."));
        int score = 0;
        int[] childMbti = new int[4];
        List<ChildMbti> childMbtis= child.getChildMbti();
        ChildMbti childMbti_val =new ChildMbti();
        for( ChildMbti childMbti_Temp : childMbtis)
        {
            if(childMbti_Temp.getDeletedAt()==null)
            {
                childMbti_val=childMbti_Temp;
                break;
            }
        }


        List<BookDto> books = bookRepository.findAllBookDto();  // 자녀 연령 필터링 및 좋아요/싫어요 고려하여 가져오기
        List<BookDto> SNrecommendedBooks = new ArrayList<>();
        List<BookDto> FTrecommendedBooks = new ArrayList<>();

        int childSnType = childMbti_val.getMbti().getSnType();  // S/N 성향
        int childTfType = childMbti_val.getMbti().getTfType();  // T/F 성향

        for (BookDto bookDto : books) {
            int bookSnType = bookDto.getBookMbti().getSnType();  // 책의 S/N 성향
            int bookTfType = bookDto.getBookMbti().getTfType();  // 책의 T/F 성향

            // S 성향 아이에게 N 성향의 책 추천 (자녀의 S 성향이 -100에 가까울수록 N 성향의 책 추천)
            if (childSnType < 0 && bookSnType >= 0) {
                SNrecommendedBooks.add(bookDto);
            }
            // N 성향 아이에게 S 성향의 책 추천 (자녀의 N 성향이 100에 가까울수록 S 성향의 책 추천)
            else if (childSnType >= 0 && bookSnType < 0) {
                SNrecommendedBooks.add(bookDto);
            }

            // F 성향 아이에게 T 성향의 책 추천 (자녀의 F 성향이 100에 가까울수록 T 성향의 책 추천)
            if (childTfType >= 0 && bookTfType < 0) {
                FTrecommendedBooks.add(bookDto);
            }
            // T 성향 아이에게 F 성향의 책 추천 (자녀의 T 성향이 -100에 가까울수록 F 성향의 책 추천)
            else if (childTfType < 0 && bookTfType >= 0) {
                FTrecommendedBooks.add(bookDto);
            }
        }

        // 추천 및 반대 성향 책 리스트 정렬
        List<BookDto> SnRecommendedBooks = sortAndSetScores(SNrecommendedBooks, child);
        List<BookDto> FtRecommendedBooks = sortAndSetScores(FTrecommendedBooks, child);

        // 자녀의 MBTI 성향(S/N, T/F)을 문자열로 변환
        String snTypeString = (childSnType < 0) ? "S" : "N";
        String tfTypeString = (childTfType < 0) ? "T" : "F";

        Map<String, Object> result = new HashMap<>();
        result.put("SNrecommendedBooks", SnRecommendedBooks);
        result.put("FTrecommendedBooks", FtRecommendedBooks);
        result.put("childSnType", snTypeString);  // 자녀의 S/N 성향
        result.put("childTfType", tfTypeString);  // 자녀의 T/F 성향

        return result;
    }

    // 추천 및 반대 성향 책 리스트 정렬 및 점수 계산
    private List<BookDto> sortAndSetScores(List<BookDto> books, Child child) {
        return books.stream()
                .peek(book -> {
                    int score = calculateWeightedScore(child, book);
                    book.setBooktotalfavor(new BookFavorTotalDto((long) score)); // 점수 설정
                })
                .sorted((b1, b2) -> {
                    int score1 = calculateWeightedScore(child, b1);
                    int score2 = calculateWeightedScore(child, b2);
                    return Integer.compare(score2, score1); // 정렬 방향
                })
                .collect(Collectors.toList());
    }
}