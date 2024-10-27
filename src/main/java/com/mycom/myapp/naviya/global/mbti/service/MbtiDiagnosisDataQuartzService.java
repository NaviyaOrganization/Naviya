package com.mycom.myapp.naviya.global.mbti.service;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.global.scheduler.ChildDeletionJob;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class MbtiDiagnosisDataQuartzService {

    private final Scheduler scheduler;

    /**
     * Child 삭제 예약 작업 스케줄링
     * @param child 자녀 엔티티
     * @param deleteDate 삭제가 예정된 날짜
     */
    public void scheduleChildDeletion(Child child, LocalDateTime deleteDate) throws SchedulerException {
        // LocalDateTime을 Date 객체로 변환하여 Quartz에서 사용할 수 있도록 함
        Date deleteAt = Date.from(deleteDate.atZone(ZoneId.systemDefault()).toInstant());

        // JobDetail 생성: 자녀 삭제 작업에 대한 정보 포함
        JobDetail jobDetail = JobBuilder.newJob(ChildDeletionJob.class)
                .withIdentity("childDeletionJob", child.getChildId().toString()) // Job 이름에 자녀 ID 사용
                .usingJobData("child", child.getChildId()) // Job에 필요한 데이터 설정 (childId)
                .usingJobData("deleteDate", deleteDate.toString()) // Job에 필요한 데이터 설정 (deleteDate)
                .build();

        // 트리거 생성: 특정 시점에 Job을 실행하도록 설정
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("childDeletionTrigger", child.getChildId().toString()) // 트리거 이름에 자녀 ID 사용
                .startAt(deleteAt) // 지정된 날짜에 Job 실행
                .build();

        // 스케줄러에 Job과 트리거를 등록하여 예약 작업 수행
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
