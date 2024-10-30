package com.mycom.myapp.naviya.service;

import com.mycom.myapp.naviya.domain.book.entity.BookFavorTotal;
import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import com.mycom.myapp.naviya.domain.child.entity.Child; // Child 엔티티를 import
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import com.mycom.myapp.naviya.domain.child.repository.ChildBookLikeRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository; // ChildRepository를 import
import com.mycom.myapp.naviya.domain.user.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
@SpringBootTest
public class BookFavorTotalConcurrencyTest {

    @Autowired
    private BookFavorService bookFavorService;
    @Autowired
    private BookServiceImpl bookServiceImpl;
    @Autowired
    private ChildRepository childRepository;

    @Test

    public void testConcurrentLikesAndDislikes() throws InterruptedException {
        Long bookId = 1L;
        String mbti = "MBTI";

        // ExecutorService로 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        ExecutorService executor = Executors.newFixedThreadPool(1000);

        long startTime = System.nanoTime(); // 시작 시간 기록

        for (int i = 1; i <= 1000; i++) {
            long childId = i;
            executor.submit(() -> {
                // 스레드 동기화 및 실행
                bookServiceImpl.ChildBookLike(childId,bookId,mbti);
                bookServiceImpl.ChildBookDisLike(childId, bookId, mbti);
            });
        }



        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES); // 모든 스레드가 종료될 때까지 대기

        long endTime = System.nanoTime(); // 종료 시간 기록
        System.out.println("Execution time: " + (endTime - startTime) + " ns");

        // 검증 - 최종적으로 BookFavorTotal count가 예상 값인지 확인
        BookFavorTotal bookFavorTotal = bookFavorService.getBookFavorTotal(bookId);
        Assertions.assertEquals(0, bookFavorTotal.getCount());



    }
}
