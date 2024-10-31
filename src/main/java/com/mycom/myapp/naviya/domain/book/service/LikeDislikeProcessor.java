package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.LikeDislikeTaskDto;
import com.mycom.myapp.naviya.domain.book.repository.BookFavorTotalRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookMbtiRepository;
import com.mycom.myapp.naviya.domain.book.repository.BookRepository;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbtiHistory;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiDto;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Component
@Service
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
    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;
    private static final String STREAM_KEY = "likeDislikeStream"; // 스트림 키
    @Autowired
    private ChildMbtiHistoryRepository childMbtiHistoryRepository;

    public void enqueueLike(Long childId, Long bookId, String type) {
        // 요청을 큐에 추가
        System.out.println("222");
        addTaskToStream(new LikeDislikeTaskDto("like",childId, bookId,type));
    }
    public void enqueueDisLike(Long childId, Long bookId, String type) {
        // 요청을 큐에 추가
        addTaskToStream(new LikeDislikeTaskDto("Dislike",childId, bookId,type));
    }
    public  void addTaskToStream(LikeDislikeTaskDto task) {
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

    @Transactional
    public void handleLike(Long childId, Long bookId, String Type) {
        try {
            System.out.println(childId);
            System.out.println(bookId);
            System.out.println(Type);
            int tempEI;
            int tempSN;
            int tempTF;
            int tempJP;
            Optional<MbtiDto> childmbtiDto =childMbtiRepository.findMbtiDtoByChildIdAndDeletedAtIsNull(childId);
            Mbti mbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                mbti.setMbtiId(childmbtiDto.get().getMbtiId());
                mbti.setEiType(childmbtiDto.get().getEiType());
                mbti.setSnType(childmbtiDto.get().getSnType());
                mbti.setTfType(childmbtiDto.get().getTfType());
                mbti.setJpType(childmbtiDto.get().getJpType());
                System.out.println(mbti.getMbtiId());
                System.out.println(mbti.getEiType());
                System.out.println(mbti.getSnType());
                System.out.println(mbti.getTfType());
                System.out.println(mbti.getJpType());
                tempEI=childmbtiDto.get().getEiType();
                tempSN=childmbtiDto.get().getSnType();
                tempTF=childmbtiDto.get().getTfType();
                tempJP=childmbtiDto.get().getJpType();
            }
            else
            {
                return;
            }
            Optional<MbtiDto> bookMbti =bookMbtiRepository.findMbtiDtoByBookId(bookId);
            Mbti Bookmbti = new Mbti();
            if (bookMbti.isPresent()) {
                Bookmbti.setMbtiId(bookMbti.get().getMbtiId());
                Bookmbti.setEiType(bookMbti.get().getEiType());
                Bookmbti.setSnType(bookMbti.get().getSnType());
                Bookmbti.setTfType(bookMbti.get().getTfType());
                Bookmbti.setJpType(bookMbti.get().getJpType());
                System.out.println(Bookmbti.getMbtiId());
                System.out.println(Bookmbti.getEiType());
                System.out.println(Bookmbti.getSnType());
                System.out.println(Bookmbti.getTfType());
                System.out.println(Bookmbti.getJpType());

            }
            else
            {
                return;
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
            } else if (Type != null && Objects.equals(Type, "NORMAL")) {
                EI = weightCal((int) (Bookmbti.getEiType() * 0.7), mbti.getEiType(), 1);
                SN = weightCal((int) (Bookmbti.getSnType() * 0.7), mbti.getSnType(), 1);
                TF = weightCal((int) (Bookmbti.getTfType() * 0.7), mbti.getTfType(), 1);
                JP = weightCal((int) (Bookmbti.getJpType() * 0.7), mbti.getJpType(), 1);
            } else {
                return;
            }
            int a=childBookLikeRepository.saveChildBooklike(childId,bookId);
            System.out.println(a);
            if(a>0) {
                mbti.setEiType(EI);
                mbti.setSnType(SN);
                mbti.setTfType(TF);
                mbti.setJpType(JP);
                System.out.println(mbti.getMbtiId());
                System.out.println(mbti.getEiType());
                System.out.println(mbti.getSnType());
                System.out.println(mbti.getTfType());
                System.out.println(mbti.getJpType());
                mbtiRepository.save(mbti);
                bookFavorTotalRepository.incrementCountByBookId(bookId);
                childBookDisLikeRepository.deleteByChild_ChildIdAndBook_BookId(childId, bookId);
                System.out.println(mbti.getMbtiId());
            }
            boolean hasSignChanged =
                    ((tempEI <= 0 && EI > 0) || (tempEI >= 0 && EI < 0)) ||
                            ((tempSN <= 0 && SN > 0) || (tempSN >= 0 && SN < 0)) ||
                            ((tempTF <= 0 && TF > 0) || (tempTF >= 0 && TF < 0)) ||
                            ((tempJP <= 0 && JP > 0) || (tempJP >= 0 && JP < 0));
            if(hasSignChanged)
            {
                String new_Mbti="";
                if(EI>0)
                {
                    new_Mbti=new_Mbti.concat("E");
                }
                else
                {
                    new_Mbti=new_Mbti.concat("I");
                }
                if(SN>0)
                {
                    new_Mbti=new_Mbti.concat("N");
                }
                else
                {
                    new_Mbti= new_Mbti.concat("S");
                }
                if(TF>0)
                {
                    new_Mbti=  new_Mbti.concat("F");
                }
                else
                {
                    new_Mbti=  new_Mbti.concat("T");
                }
                if(JP>0)
                {
                    new_Mbti= new_Mbti.concat("P");
                }
                else
                {
                    new_Mbti=new_Mbti.concat("J");
                }
                System.out.println(new_Mbti);
                Child child=childRepository.findByChildId(childId);
                child.setCodeMbti(new_Mbti);
                ChildMbtiHistory childMbtiHistory = new ChildMbtiHistory();
                childMbtiHistory.setDeletedAt(null);
                childMbtiHistory.setCodeNewMbti(new_Mbti);
                childMbtiHistory.setUpdatedAt(LocalDateTime.now());
                childMbtiHistory.setChild(child);
                childMbtiHistoryRepository.save(childMbtiHistory);
                childRepository.save(child);
            }

        }catch(Exception e){
        }
    }
@Transactional
public void handleDislike(Long childId, Long bookId, String Type) {

        try {

            Optional<MbtiDto> childmbtiDto =childMbtiRepository.findMbtiDtoByChildIdAndDeletedAtIsNull(childId);
            System.out.println("싫엉요");
            int tempEI;
            int tempSN;
            int tempTF;
            int tempJP;
            Mbti mbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                mbti.setMbtiId(childmbtiDto.get().getMbtiId());
                mbti.setEiType(childmbtiDto.get().getEiType());
                mbti.setSnType(childmbtiDto.get().getSnType());
                mbti.setTfType(childmbtiDto.get().getTfType());
                mbti.setJpType(childmbtiDto.get().getJpType());
                System.out.println(mbti.getMbtiId());
                System.out.println(mbti.getEiType());
                System.out.println(mbti.getSnType());
                System.out.println(mbti.getTfType());
                System.out.println(mbti.getJpType());
                tempEI=childmbtiDto.get().getEiType();
                tempSN=childmbtiDto.get().getSnType();
                tempTF=childmbtiDto.get().getTfType();
                tempJP=childmbtiDto.get().getJpType();
            }
            else
            {
                return;
            }



            Optional<MbtiDto> mbtiDto =bookMbtiRepository.findMbtiDtoByBookId(bookId);
            Mbti Bookmbti = new Mbti();
            if (childmbtiDto.isPresent()) {
                Bookmbti.setMbtiId(mbtiDto.get().getMbtiId());
                Bookmbti.setEiType(mbtiDto.get().getEiType());
                Bookmbti.setSnType(mbtiDto.get().getSnType());
                Bookmbti.setTfType(mbtiDto.get().getTfType());
                Bookmbti.setJpType(mbtiDto.get().getJpType());
                System.out.println(Bookmbti.getMbtiId());
                System.out.println(Bookmbti.getEiType());
                System.out.println(Bookmbti.getSnType());
                System.out.println(Bookmbti.getTfType());
                System.out.println(Bookmbti.getJpType());
            }
            else
            {
                return;
            }

            int EI;
            int SN;
            int TF;
            int JP;
            System.out.println(Type);
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
            } else if (Type != null && Objects.equals(Type, "NORMAL")) {
                EI = weightCal((int) (Bookmbti.getEiType() * 0.7), mbti.getEiType(), -1);
                SN = weightCal((int) (Bookmbti.getSnType() * 0.7), mbti.getSnType(), -1);
                TF = weightCal((int) (Bookmbti.getTfType() * 0.7), mbti.getTfType(), -1);
                JP = weightCal((int) (Bookmbti.getJpType() * 0.7), mbti.getJpType(), -1);
            } else {

                return;
            }

            if(childBookDisLikeRepository.saveChildBookDislike(childId, bookId)>0) {
                mbti.setEiType(EI);
                mbti.setSnType(SN);
                mbti.setTfType(TF);
                mbti.setJpType(JP);
                mbtiRepository.save(mbti);
                bookFavorTotalRepository.decrementCountByBookId(bookId);
                childBookLikeRepository.deleteByChildIdAndBookIdAndDelDateIsNull(childId, bookId);
                System.out.println("qw");
            }
            boolean hasSignChanged =
                    ((tempEI <= 0 && EI > 0) || (tempEI >= 0 && EI < 0)) ||
                            ((tempSN <= 0 && SN > 0) || (tempSN >= 0 && SN < 0)) ||
                            ((tempTF <= 0 && TF > 0) || (tempTF >= 0 && TF < 0)) ||
                            ((tempJP <= 0 && JP > 0) || (tempJP >= 0 && JP < 0));
            if(hasSignChanged)
            {
                String new_Mbti="";
                if(EI>0)
                {
                    new_Mbti=new_Mbti.concat("E");
                }
                else
                {
                    new_Mbti=new_Mbti.concat("I");
                }
                if(SN>0)
                {
                    new_Mbti=new_Mbti.concat("N");
                }
                else
                {
                    new_Mbti= new_Mbti.concat("S");
                }
                if(TF>0)
                {
                    new_Mbti=  new_Mbti.concat("F");
                }
                else
                {
                    new_Mbti=  new_Mbti.concat("T");
                }
                if(JP>0)
                {
                    new_Mbti= new_Mbti.concat("P");
                }
                else
                {
                    new_Mbti=new_Mbti.concat("J");
                }
                System.out.println(new_Mbti);
                Child child=childRepository.findByChildId(childId);
                child.setCodeMbti(new_Mbti);
                ChildMbtiHistory childMbtiHistory = new ChildMbtiHistory();
                childMbtiHistory.setDeletedAt(null);
                childMbtiHistory.setCodeNewMbti(new_Mbti);
                childMbtiHistory.setUpdatedAt(LocalDateTime.now());
                childMbtiHistory.setChild(child);
                childMbtiHistoryRepository.save(childMbtiHistory);
                childRepository.save(child);
            }

        }catch (Exception e){
        }
    }

}
