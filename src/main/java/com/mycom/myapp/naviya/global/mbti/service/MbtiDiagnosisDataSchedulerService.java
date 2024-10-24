package com.mycom.myapp.naviya.global.mbti.service;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Service
@AllArgsConstructor
public class MbtiDiagnosisDataSchedulerService {

    private final TaskScheduler taskScheduler;
    private final ChildMbtiRepository childMbtiRepository;
    private final ChildMbtiHistoryRepository childMbtiHistoryRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    private final ChildBookDisLikeRepository childBookDislikeRepository;
    private final ChildFavorCategoryRepository childFavorCategoryRepository;


    /**
     * Child 삭제 예약 작업 스케줄링
     * @param child 자녀 엔티티
     * @param deleteDate 삭제가 예정된 날짜
     */
    public void scheduleChildDeletion(Child child, LocalDateTime deleteDate) {
        // LocalDateTime을 Date로 변환
        Date deleteAt = Date.from(deleteDate.atZone(ZoneId.systemDefault()).toInstant());

        // 30일 후 삭제 작업 예약
        taskScheduler.schedule(() -> deleteChildRecords(child, deleteDate), deleteAt);
    }

    private void deleteChildRecords(Child child, LocalDateTime futureLocalDateTime) {
        // 실제 ChildMbti, ChildMbtiHistory, ChildBookLike 등에서 삭제 작업 수행
        // ChildMbti의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childMbtiRepository.deleteByChildAndDeletedAt(child, futureLocalDateTime);

        // ChildMbtiHistory의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childMbtiHistoryRepository.deleteByChildAndDeletedAt(child, futureLocalDateTime);

        // ChildBookLike의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childBookLikeRepository.deleteByChildAndDeletedAt(child, futureLocalDateTime);

        // ChildBookDislike의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childBookDislikeRepository.deleteByChildAndDeletedAt(child, futureLocalDateTime);

        // ChildFavorCategory의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childFavorCategoryRepository.deleteByChildAndDeletedAt(child, futureLocalDateTime);
    }

}
