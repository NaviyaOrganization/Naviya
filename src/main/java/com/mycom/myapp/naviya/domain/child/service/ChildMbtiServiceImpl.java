package com.mycom.myapp.naviya.domain.child.service;

import com.mycom.myapp.naviya.domain.child.dto.MBTIScoresDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbtiHistory;
import com.mycom.myapp.naviya.domain.child.repository.ChildMbtiHistoryRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildMbtiRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ChildMbtiServiceImpl implements ChildMbtiService {

    @Autowired
    private ChildMbtiRepository childMbtiRepository;
    @Autowired
    private ChildRepository childRepository;
    @Autowired
    private MbtiRepository mbtiRepository;
    @Autowired
    private ChildMbtiHistoryRepository childMbtiHistoryRepository;

    /**
     * 자녀의 MBTI 성향을 진단하고 저장하는 메서드 (ChildMbti와 ChildMbtiHistory에 저장).
     *
     * @param childId 자녀의 ID
     * @param scores  자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     */
    @Transactional
    public void createChildMbti(String childId, MBTIScoresDto scores) {
        // Child 조회
        Child child = childRepository.findById(Long.valueOf(childId))
                .orElseThrow(() -> new RuntimeException("아이를 찾을 수 없습니다."));

        // 기존 데이터 처리: ChildMbti와 ChildMbtiHistory의 deletedAt 필드 업데이트(논리적 삭제)
        updateDeletedAtForExistingRecords(child);

        // 새로운 MBTI 데이터 저장
        saveNewMbti(child, scores);

        // 히스토리 데이터 생성 및 저장
        createChildMbtiHistory(child, scores);
    }

    /**
     * 기존 ChildMbti 및 ChildMbtiHistory에서 deletedAt이 null인 데이터를
     * 현재 시점으로부터 30일 뒤로 업데이트하는 메서드.
     *
     * @param child 자녀 엔티티
     */
    private void updateDeletedAtForExistingRecords(Child child) {
        // 현재 시간 기준으로 30일 뒤 시간 설정
        Timestamp futureTimestamp = Timestamp.valueOf(LocalDateTime.now().plusDays(30));

        // ChildMbti와 Mbti를 함께 조회하여 삭제 처리
        List<ChildMbti> existingChildMbtis = childMbtiRepository.findByChildAndDeletedAtIsNull(child);
        for (ChildMbti existingChildMbti : existingChildMbtis) {
            existingChildMbti.setDeletedAt(futureTimestamp);
            childMbtiRepository.save(existingChildMbti);
        }

        // ChildMbtiHistory의 deletedAt을 업데이트
        List<ChildMbtiHistory> existingHistories = childMbtiHistoryRepository.findByChildAndDeletedAtIsNull(child);
        for (ChildMbtiHistory history : existingHistories) {
            history.setDeletedAt(futureTimestamp);
            childMbtiHistoryRepository.save(history);
        }
    }


    /**
     * 새로운 MBTI 데이터를 ChildMbti에 저장하고 자녀의 MBTI 코드를 업데이트하는 메서드.
     *
     * @param child  자녀 엔티티
     * @param scores 자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     */
    private void saveNewMbti(Child child, MBTIScoresDto scores) {
        // 새로운 ChildMbti 및 Mbti 생성
        ChildMbti childMbti = new ChildMbti();
        Mbti mbti = new Mbti();

        // 연관 관계 설정
        childMbti.setChild(child);
        childMbti.setMbti(mbti);

        // MBTI 성향 업데이트 (점수에 따른 설정)
        int minusScore = -5;
        int plusScore = 5;
        mbti.setEiType(scores.getEiScore().equals("I") ? minusScore : plusScore); // I - / E +
        mbti.setSnType(scores.getSnScore().equals("S") ? minusScore : plusScore); // S - / N +
        mbti.setTfType(scores.getTfScore().equals("T") ? minusScore : plusScore); // T - / F +
        mbti.setJpType(scores.getJpScore().equals("J") ? minusScore : plusScore); // J - / P +

        child.setCodeMbti(calculateMBTI(scores));
        childMbtiRepository.save(childMbti);
        // MBTI 및 ChildMbti 저장
        mbtiRepository.save(mbti);
        childMbtiRepository.save(childMbti);
    }



    /**
     * 진단 정보를 바탕으로 ChildMbtiHistory 엔티티를 생성하고 저장하는 메서드.
     *
     * @param child  자녀 엔티티
     * @param scores 자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     */
    @Transactional
    public void createChildMbtiHistory(Child child, MBTIScoresDto scores) {
        // 새로운 ChildMbtiHistory 생성
        ChildMbtiHistory childMbtiHistory = new ChildMbtiHistory();

        // 연관 관계 설정
        childMbtiHistory.setChild(child);
        childMbtiHistory.setCodeNewMbti(calculateMBTI(scores));
        childMbtiHistory.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // 새로운 히스토리 저장
        childMbtiHistoryRepository.save(childMbtiHistory);
    }

    /**
     * MBTI 점수 데이터를 바탕으로 MBTI 코드를 생성하는 메서드 (I/E, S/N, T/F, J/P).
     *
     * @param scores 자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     * @return MBTI 코드 (예: "ISTP", "ENFP")
     */
    public String calculateMBTI(MBTIScoresDto scores) {
        // I/E, S/N, T/F, J/P 성향 그대로 사용
        return scores.getEiScore() + scores.getSnScore() + scores.getTfScore() + scores.getJpScore();
    }

    /**
     * MBTI 테이블에 저장된 데이터를 기반으로 ChildMbtiHistory를 생성하고 자녀의 MBTI 코드를 업데이트하는 메서드.
     *
     * @param child 자녀 엔티티
     */
    @Transactional
    public void createChildMbtiHistory(Child child) {
        // Child에 연관된 ChildMbti와 Mbti를 한 번의 조회로 가져오기
        ChildMbti childMbti = childMbtiRepository.findByChildWithMbti(child)
                .orElseThrow(() -> new RuntimeException("해당 아이의 MBTI 데이터가 없습니다."));

        // ChildMbtiHistory 생성
        ChildMbtiHistory childMbtiHistory = new ChildMbtiHistory();

        // 연관관계 설정
        childMbtiHistory.setChild(child);

        // MBTI 결과를 기반으로 새 MBTI 코드 생성 (I/E, S/N, T/F, J/P 값을 조합)
        String newMbtiCode = calculateMBTI(childMbti.getMbti());
        childMbtiHistory.setCodeNewMbti(newMbtiCode);

        // updatedAt 시간 저장
        childMbtiHistory.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // ChildMbtiHistory 저장
        childMbtiHistoryRepository.save(childMbtiHistory);

        // Child의 codeMbti 필드도 업데이트
        child.setCodeMbti(newMbtiCode);

        // Child 엔티티 저장
        childRepository.save(child);
    }

    /**
     * MBTI 엔티티 데이터를 바탕으로 MBTI 코드를 생성하는 메서드 (I/E, S/N, T/F, J/P).
     *
     * @param mbti MBTI 엔티티 객체
     * @return MBTI 코드 (예: "ISTP", "ENFP")
     */
    public String calculateMBTI(Mbti mbti) {
        String ei = (mbti.getEiType() < 0) ? "I" : "E";
        String sn = (mbti.getSnType() < 0) ? "S" : "N";
        String tf = (mbti.getTfType() < 0) ? "T" : "F";
        String jp = (mbti.getJpType() < 0) ? "J" : "P";

        return ei + sn + tf + jp; // ISTP, ENFP 등 MBTI 코드 생성
    }

//    /**
//     * ChildMbtiHistory에서 가장 최근의 MBTI를 조회하고 Child에 저장하는 메서드
//     */
//    @Transactional
//    public void updateChildWithLatestMbti(String childId) {
//        // Child 조회
//        Child child = childRepository.findById(Long.valueOf(childId))
//                .orElseThrow(() -> new RuntimeException("아이를 찾을 수 없습니다."));
//
//        // Child에 대한 가장 최근의 ChildMbtiHistory 조회
//        ChildMbtiHistory latestMbtiHistory = childMbtiHistoryRepository.findLatestMbtiHistoryByChild(child);
//
//        if (latestMbtiHistory != null) {
//            // 최신 MBTI 값을 Child의 codeMbti에 저장
//            child.setCodeMbti(latestMbtiHistory.getCodeNewMbti());
//
//            // updatedAt 필드 갱신
//            child.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//            // Child 엔티티 저장
//            childRepository.save(child);
//        } else {
//            throw new RuntimeException("해당 아이의 MBTI 기록이 없습니다.");
//        }
//    }


}

