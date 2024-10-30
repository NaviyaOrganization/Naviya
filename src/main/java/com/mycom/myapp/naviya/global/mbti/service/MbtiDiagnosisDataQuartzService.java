package com.mycom.myapp.naviya.global.mbti.service;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.global.scheduler.ChildDeletionJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class MbtiDiagnosisDataQuartzService {

    private final Scheduler scheduler;

    /**
     * Child 삭제 예약 작업 스케줄링
     * @param child 자녀 엔티티
     * @param deleteDate 삭제가 예정된 날짜
     */
    public void scheduleChildDeletion(Child child, LocalDateTime deleteDate) {
        synchronized (this) {
            try {
                // LocalDateTime을 Date 객체로 변환하여 Quartz에서 사용할 수 있도록 함
                Date deleteAt = Date.from(deleteDate.atZone(ZoneId.systemDefault()).toInstant());
                String jobName = "child-" + child.getChildId().toString() + "-" + deleteDate;
                JobKey jobKey = new JobKey(jobName, "childDeletionJob");

                // 기존에 동일한 Job이 존재하는지 확인하고, 있을 경우 삭제
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                    log.info("기존 Job이 삭제되었습니다: {}", jobKey);
                }

                // JobDetail 생성: 자녀 삭제 작업에 대한 정보 포함
                JobDetail jobDetail = JobBuilder.newJob(ChildDeletionJob.class)
                        .withIdentity(jobKey) // Job 이름에 자녀 ID 사용
                        .usingJobData("child", child.getChildId()) // Job에 필요한 데이터 설정 (childId)
                        .usingJobData("deleteDate", deleteDate.toString()) // Job에 필요한 데이터 설정 (deleteDate)
                        .build();

                // 트리거 생성: 특정 시점에 Job을 실행하도록 설정
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(child.getChildId().toString() + " " + deleteDate, "childDeletionTrigger") // 트리거 이름에 자녀 ID 사용
                        .startAt(deleteAt) // 지정된 날짜에 Job 실행
                        .build();

                // 스케줄러에 Job과 트리거를 등록하여 예약 작업 수행
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("Child 삭제 작업이 스케줄되었습니다: childId={}, deleteDate={}", child.getChildId(), deleteDate);
            } catch (SchedulerException e) {
                log.error("Child 삭제 작업 스케줄링 실패: childId={}, deleteDate={}", child.getChildId(), deleteDate, e);
                throw new RuntimeException("Failed to schedule child deletion", e);
            }
        }
    }

    /**
     * 특정 childId에 관련된 모든 Job 삭제
     * @param childId 자녀 ID
     */
    public void deleteAllJobsForChild(Long childId) {
        try {
            // "childDeletionJob" 그룹 내 모든 JobKey 순회
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("childDeletionJob"))) {
                // jobName이 "child-{childId}-" 형식을 사용해 정확히 일치하는지 확인
                if (jobKey.getName().startsWith("child-" + childId + "-")) {
                    // 해당 Job 삭제
                    scheduler.deleteJob(jobKey);
                    log.info("Job이 삭제되었습니다: {}", jobKey);
                }
            }
        } catch (SchedulerException e) {
            log.error("예약된 삭제 작업 취소 중 오류 발생: childId={}", childId, e);
            throw new RuntimeException("예약된 삭제 작업 취소 중 오류 발생", e);
        }
    }
}
