package com.mycom.myapp.naviya.global.scheduler;

import com.mycom.myapp.naviya.domain.child.repository.*;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@PersistJobDataAfterExecution
// 자녀 삭제 작업을 정의하는 Quartz Job 클래스
public class ChildDeletionJob implements Job {

    private final ChildMbtiRepository childMbtiRepository;
    private final ChildMbtiHistoryRepository childMbtiHistoryRepository;
    private final ChildBookLikeRepository childBookLikeRepository;
    private final ChildBookDisLikeRepository childBookDislikeRepository;
    private final ChildFavorCategoryRepository childFavorCategoryRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // JobExecutionContext에서 전달된 childId 가져오기
        Long childId = context.getMergedJobDataMap().getLong("child");

        // JobExecutionContext에서 전달된 deleteDate 가져오기
        LocalDateTime futureLocalDateTime = LocalDateTime.parse(context.getMergedJobDataMap().getString("deleteDate"));

        // 실제 ChildMbti, ChildMbtiHistory, ChildBookLike 등에서 삭제 작업 수행

        // ChildMbti의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childMbtiRepository.deleteByChildIdAndDeletedAt(childId, futureLocalDateTime);

        // ChildMbtiHistory의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childMbtiHistoryRepository.deleteByChildIdAndDeletedAt(childId, futureLocalDateTime);

        // ChildBookLike의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childBookLikeRepository.deleteByChildIdAndDeletedAt(childId, futureLocalDateTime);

        // ChildBookDislike의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childBookDislikeRepository.deleteByChildIdAndDeletedAt(childId, futureLocalDateTime);

        // ChildFavorCategory의 deletedAt이 futureLocalDateTime인 데이터 삭제
        childFavorCategoryRepository.deleteByChildIdAndDeletedAt(childId, futureLocalDateTime);
    }

}
