package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.dto.LikeDislikeTaskDto;
import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.book.entity.BookMbti;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookMbtiRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookDislike;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
@RequiredArgsConstructor
@Component
public class LikeDislikeProcessor {
    private final BookRepository bookRepository;
    private final MbtiRepository mbtiRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    private final ChildBookDisLikeRepository childBookDisLikeRepository;
    private final BookFavorTotalRepository bookFavorTotalRepository;
    private final BookMbtiRepository bookMbtiRepository;
    private final ChildRepository childRepository;
    private final ChildFavorCategoryRepository childFavorCategoryRepository;
    private final ChildMbtiRepository childMbtiRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final String QUEUE_NAME = "likeDislikeQueue";

    @Scheduled(fixedDelay = 500) // 500ms마다 큐 확인
    public void processQueue() {
        while (true) {
            Object taskObject = redisTemplate.opsForList().leftPop(QUEUE_NAME);

            if (taskObject != null) {
                LikeDislikeTaskDto task = (LikeDislikeTaskDto) taskObject;
                String method = task.getMethod();
                Long childId = task.getChildId();
                Long bookId = task.getBookId();
                String type = task.getType();

                // 메소드 호출
                if ("like".equals(method)) {
                    handleLike(childId, bookId, type);
                } else if ("dislike".equals(method)) {
                    handleDislike(childId, bookId, type);
                }
            } else {
                try {
                    // 대기 시간을 두고 다시 큐를 확인 (예: 100ms)
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 스레드 인터럽트 처리
                    break; // 예외 발생 시 루프 종료
                }
            }
        }
    }

    public int weightCal(int calWeight,int weight,int sign)
    {
        Double baseAdjustment = (double) calWeight;
        Double currentWeight = (double) weight;
        //절대값이라 현재 mbti 가중치가 음수여도 상관 없음
        int dynamicAdjustment = (int) (baseAdjustment * (1.2 - Math.abs(currentWeight / 100)));
        int newWeight = Math.max(-100, Math.min(100, (int)(currentWeight + (dynamicAdjustment*sign))));
        return newWeight;
    }


    private boolean handleLike(Long childId, Long bookId,String Type) {
        try {
            Optional child = childRepository.findById(childId);
            Child child1 = new Child();
            if (child.isPresent()) {
                child1 = (Child) child.get();
            } else {
                return false;
            }
            Optional book = bookRepository.findById(bookId);
            Book book1 = new Book();
            if (book.isPresent()) {
                book1 = (Book) book.get();
            } else {
                return false;
            }


           /* System.out.println("e");
            List<ChildMbti> childMbtis = child1.getChildMbti();
            if (childMbtis == null) {
                System.out.println("d실패");
                return false;
            }
            ChildMbti childMbti_val = new ChildMbti();

            int flag = 0;
            for (ChildMbti childMbti_Temp : childMbtis) {
                if (childMbti_Temp.getDeletedAt() == null) {
                    childMbti_val = childMbti_Temp;
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                System.out.println("2");
                return false;
            }
            System.out.println("q");

            BookMbti bookMbti = book1.getBookMbti();
            Mbti book2Mbti = bookMbti.getMbti();
            Mbti mbti = childMbti_val.getMbti();

            int EI;
            int SN;
            int TF;
            int JP;
            if (Type != null && Objects.equals(Type, "MBTI")) {
                EI = weightCal(book2Mbti.getEiType(), mbti.getEiType(), 1);
                SN = weightCal(book2Mbti.getSnType(), mbti.getSnType(), 1);
                TF = weightCal(book2Mbti.getTfType(), mbti.getTfType(), 1);
                JP = weightCal(book2Mbti.getJpType(), mbti.getJpType(), 1);
            } else if (Type != null && Objects.equals(Type, "REVERSE")) {
                EI = weightCal((int) (book2Mbti.getEiType() * 1.2), mbti.getEiType(), 1);
                SN = weightCal((int) (book2Mbti.getSnType() * 1.2), mbti.getSnType(), 1);
                TF = weightCal((int) (book2Mbti.getTfType() * 1.2), mbti.getTfType(), 1);
                JP = weightCal((int) (book2Mbti.getJpType() * 1.2), mbti.getJpType(), 1);
            } else if (Type != null && Objects.equals(Type, "NOMAL")) {
                EI = weightCal((int) (book2Mbti.getEiType() * 0.7), mbti.getEiType(), 1);
                SN = weightCal((int) (book2Mbti.getSnType() * 0.7), mbti.getSnType(), 1);
                TF = weightCal((int) (book2Mbti.getTfType() * 0.7), mbti.getTfType(), 1);
                JP = weightCal((int) (book2Mbti.getJpType() * 0.7), mbti.getJpType(), 1);
            } else {
                System.out.println("3");
                return false;
            }*/
            ChildBookLike childBookLike = new ChildBookLike();
            childBookLike.setChild(child1);
            childBookLike.setBook(book1);
            childBookLike.setDeletedAt(null);
            childBookLikeRepository.save(childBookLike); // ChildBookLike만 명시적으로 저장
            bookFavorTotalRepository.incrementCountByBookId(bookId);
            // mbti는 이미 조회된 상태에서 변경되었으므로, save 없이도 트랜잭션이 끝나면 자동 반영
            /*mbti.setEiType(EI);
            mbti.setSnType(SN);
            mbti.setTfType(TF);
            mbti.setJpType(JP);
            mbtiRepository.save(mbti);*/
            childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(childId, bookId);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private boolean handleDislike(Long childId, Long bookId,String Type) {

        try {
            Optional child = childRepository.findById(childId);
            Child child1 = new Child();
            if (child.isPresent()) {
                child1 = (Child) child.get();
            } else {
                return false;
            }
            Optional book = bookRepository.findById(bookId);
            Book book1 = new Book();
            if (book.isPresent()) {
                book1 = (Book) book.get();
            } else {
                return false;
            }
            /*List<ChildMbti> childMbtis = child1.getChildMbti();
            ChildMbti childMbti_val = new ChildMbti();
            int flag = 0;

            for (ChildMbti childMbti_Temp : childMbtis) {
                if (childMbti_Temp.getDeletedAt() == null) {
                    childMbti_val = childMbti_Temp;
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                return false;
            }

            Mbti mbti = childMbti_val.getMbti();

            BookMbti bookMbti = book1.getBookMbti();
            Mbti book2Mbti = bookMbti.getMbti();

            int EI;
            int SN;
            int TF;
            int JP;
            if (Type != null && Objects.equals(Type, "MBTI")) {
                EI = weightCal(book2Mbti.getEiType(), mbti.getEiType(), -1);
                SN = weightCal(book2Mbti.getSnType(), mbti.getSnType(), -1);
                TF = weightCal(book2Mbti.getTfType(), mbti.getTfType(), -1);
                JP = weightCal(book2Mbti.getJpType(), mbti.getJpType(), -1);
            } else if (Type != null && Objects.equals(Type, "REVERSE")) {
                EI = weightCal((int) (book2Mbti.getEiType() * 1.2), mbti.getEiType(), -1);
                SN = weightCal((int) (book2Mbti.getSnType() * 1.2), mbti.getSnType(), -1);
                TF = weightCal((int) (book2Mbti.getTfType() * 1.2), mbti.getTfType(), -1);
                JP = weightCal((int) (book2Mbti.getJpType() * 1.2), mbti.getJpType(), -1);
            } else if (Type != null && Objects.equals(Type, "NOMAL")) {
                EI = weightCal((int) (book2Mbti.getEiType() * 0.7), mbti.getEiType(), -1);
                SN = weightCal((int) (book2Mbti.getSnType() * 0.7), mbti.getSnType(), -1);
                TF = weightCal((int) (book2Mbti.getTfType() * 0.7), mbti.getTfType(), -1);
                JP = weightCal((int) (book2Mbti.getJpType() * 0.7), mbti.getJpType(), -1);
            } else {

                return false;
            }*/

            ChildBookDislike childBookDislike = new ChildBookDislike();
            childBookDislike.setChild(child1);
            childBookDislike.setBook(book1);
            bookFavorTotalRepository.decrementCountByBookId(bookId);
            childBookDisLikeRepository.save(childBookDislike);

            /*mbti.setEiType(EI);
            mbti.setSnType(SN);
            mbti.setTfType(TF);
            mbti.setJpType(JP);
            mbtiRepository.save(mbti);*/
            childBookLikeRepository.deleteByChildIdAndBookIdAndDelDateIsNull(childId, bookId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
