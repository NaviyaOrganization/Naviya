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
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private  RedisTemplate<String, Object> redisTemplate;
    private static final String STREAM_KEY = "likeDislikeStream"; // 스트림 키

    public void enqueueLike(Long childId, Long bookId, String type) {
        // 요청을 큐에 추가
        addTaskToStream(new LikeDislikeTaskDto("like",childId, bookId,type));
    }
    public void enqueueDisLike(Long childId, Long bookId,String type) {
        // 요청을 큐에 추가
        addTaskToStream(new LikeDislikeTaskDto("Dislike",childId, bookId,type));
    }
    public void addTaskToStream(LikeDislikeTaskDto task) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("method", task.getMethod());
        taskMap.put("childId", task.getChildId().toString());
        taskMap.put("bookId", task.getBookId().toString());
        taskMap.put("type", task.getType());

        redisTemplate.opsForStream().add(STREAM_KEY, taskMap);
    }
    @Scheduled(fixedDelay = 500)
    public void scheduleStreamTaskProcessing() {
        processStreamTasks();
    }
    public void processStreamTasks() {

        while (true) {
            // Stream에서 메시지를 읽습니다.
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(
                            Consumer.from("likeDislikeGroup", "consumer1"),
                            StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()) // 마지막으로 읽은 오프셋부터 읽기
                    );

            // 읽은 레코드가 null이 아니고 비어있지 않은 경우에만 처리
            if (records != null && !records.isEmpty()) {
                for (MapRecord<String, Object, Object> record : records) {
                    try {
                        Map<Object, Object> valueMap = record.getValue();
                        String method = (String) valueMap.get("method");
                        Long childId = Long.valueOf((String) valueMap.get("childId"));
                        Long bookId = Long.valueOf((String) valueMap.get("bookId"));
                        String type = (String) valueMap.get("type");

                        LikeDislikeTaskDto task = new LikeDislikeTaskDto(method, childId, bookId, type);
                        if ("like".equals(task.getMethod())) {
                            handleLike(task.getChildId(), task.getBookId(), task.getType());
                        } else if ("Dislike".equals(task.getMethod())) {
                            handleDislike(task.getChildId(), task.getBookId(), task.getType());
                        }

                        // 처리한 레코드를 확인(acknowledge)
                        redisTemplate.opsForStream().acknowledge(STREAM_KEY, "likeDislikeGroup", record.getId());
                    } catch (Exception e) {
                        // 예외가 발생하면 로깅하거나 적절히 처리
                        System.err.println("Error processing record: " + record.getId() + ", Error: " + e.getMessage());
                    }
                }
            } else {
                // 레코드가 없을 경우 잠시 대기
                try {
                    Thread.sleep(100); // 0.1초 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 인터럽트 복구
                    break; // 루프 종료
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
            Optional<MbtiDto> childmbtiDto =childMbtiRepository.findMbtiDtoByChildIdAndDeletedAtIsNull(childId);
            Mbti mbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                mbti.setMbtiId(childmbtiDto.get().getMbtiId());
                mbti.setEiType(childmbtiDto.get().getEiType());
                mbti.setSnType(childmbtiDto.get().getSnType());
                mbti.setTfType(childmbtiDto.get().getTfType());
                mbti.setJpType(childmbtiDto.get().getJpType());
            }
            else
            {
                return false;
            }
            Optional<MbtiDto> mbtiDto =bookMbtiRepository.findMbtiDtoByBookId(bookId);
            Mbti Bookmbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                Bookmbti.setMbtiId(mbtiDto.get().getMbtiId());
                Bookmbti.setEiType(mbtiDto.get().getEiType());
                Bookmbti.setSnType(mbtiDto.get().getSnType());
                Bookmbti.setTfType(mbtiDto.get().getTfType());
                Bookmbti.setJpType(mbtiDto.get().getJpType());

            }
            else
            {
                return false;
            }

            int EI;
            int SN;
            int TF;
            int JP;
            if (Type != null && Objects.equals(Type, "MBTI")) {
                EI = weightCal(Bookmbti.getEiType(), mbti.getEiType(), 1);
                SN = weightCal(Bookmbti.getSnType(), mbti.getSnType(), 1);
                TF = weightCal(Bookmbti.getTfType(), mbti.getTfType(), 1);
                JP = weightCal(Bookmbti.getJpType(), mbti.getJpType(), 1);
            } else if (Type != null && Objects.equals(Type, "REVERSE")) {
                EI = weightCal((int) (Bookmbti.getEiType() * 1.2), mbti.getEiType(), 1);
                SN = weightCal((int) (Bookmbti.getSnType() * 1.2), mbti.getSnType(), 1);
                TF = weightCal((int) (Bookmbti.getTfType() * 1.2), mbti.getTfType(), 1);
                JP = weightCal((int) (Bookmbti.getJpType() * 1.2), mbti.getJpType(), 1);
            } else if (Type != null && Objects.equals(Type, "NOMAL")) {
                EI = weightCal((int) (Bookmbti.getEiType() * 0.7), mbti.getEiType(), 1);
                SN = weightCal((int) (Bookmbti.getSnType() * 0.7), mbti.getSnType(), 1);
                TF = weightCal((int) (Bookmbti.getTfType() * 0.7), mbti.getTfType(), 1);
                JP = weightCal((int) (Bookmbti.getJpType() * 0.7), mbti.getJpType(), 1);
            } else {
                return false;
            }
            if(childBookLikeRepository.saveChildBooklike(childId,bookId)>0) {
                mbti.setEiType(EI);
                mbti.setSnType(SN);
                mbti.setTfType(TF);
                mbti.setJpType(JP);
                mbtiRepository.save(mbti);
                bookFavorTotalRepository.incrementCountByBookId(bookId);
                childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(childId, bookId);
                return true;
            }
            return false;

        }catch(Exception e){
            return false;
        }
    }

    private boolean handleDislike(Long childId, Long bookId, String Type) {

        try {

            Optional<MbtiDto> childmbtiDto =childMbtiRepository.findMbtiDtoByChildIdAndDeletedAtIsNull(childId);
            Mbti mbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                mbti.setMbtiId(childmbtiDto.get().getMbtiId());
                mbti.setEiType(childmbtiDto.get().getEiType());
                mbti.setSnType(childmbtiDto.get().getSnType());
                mbti.setTfType(childmbtiDto.get().getTfType());
                mbti.setJpType(childmbtiDto.get().getJpType());
            }
            else
            {
                return false;
            }



            Optional<MbtiDto> mbtiDto =bookMbtiRepository.findMbtiDtoByBookId(bookId);
            Mbti Bookmbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                Bookmbti.setMbtiId(mbtiDto.get().getMbtiId());
                Bookmbti.setEiType(mbtiDto.get().getEiType());
                Bookmbti.setSnType(mbtiDto.get().getSnType());
                Bookmbti.setTfType(mbtiDto.get().getTfType());
                Bookmbti.setJpType(mbtiDto.get().getJpType());

            }
            else
            {
                return false;
            }

            int EI;
            int SN;
            int TF;
            int JP;
            if (Type != null && Objects.equals(Type, "MBTI")) {
                EI = weightCal(Bookmbti.getEiType(), mbti.getEiType(), -1);
                SN = weightCal(Bookmbti.getSnType(), mbti.getSnType(), -1);
                TF = weightCal(Bookmbti.getTfType(), mbti.getTfType(), -1);
                JP = weightCal(Bookmbti.getJpType(), mbti.getJpType(), -1);
            } else if (Type != null && Objects.equals(Type, "REVERSE")) {
                EI = weightCal((int) (Bookmbti.getEiType() * 1.2), mbti.getEiType(), -1);
                SN = weightCal((int) (Bookmbti.getSnType() * 1.2), mbti.getSnType(), -1);
                TF = weightCal((int) (Bookmbti.getTfType() * 1.2), mbti.getTfType(), -1);
                JP = weightCal((int) (Bookmbti.getJpType() * 1.2), mbti.getJpType(), -1);
            } else if (Type != null && Objects.equals(Type, "NOMAL")) {
                EI = weightCal((int) (Bookmbti.getEiType() * 0.7), mbti.getEiType(), -1);
                SN = weightCal((int) (Bookmbti.getSnType() * 0.7), mbti.getSnType(), -1);
                TF = weightCal((int) (Bookmbti.getTfType() * 0.7), mbti.getTfType(), -1);
                JP = weightCal((int) (Bookmbti.getJpType() * 0.7), mbti.getJpType(), -1);
            } else {

                return false;
            }

            if(childBookDisLikeRepository.saveChildBookDislike(childId, bookId)>0) {
                mbti.setEiType(EI);
                mbti.setSnType(SN);
                mbti.setTfType(TF);
                mbti.setJpType(JP);
                mbtiRepository.save(mbti);
                bookFavorTotalRepository.decrementCountByBookId(bookId);
                childBookLikeRepository.deleteByChildIdAndBookIdAndDelDateIsNull(childId, bookId);
                return true;
            }
                return false;

        }catch (Exception e){
            return false;
        }
    }

}
