package com.mycom.myapp.naviya.domain.child.service;

import com.mycom.myapp.naviya.domain.child.dto.*;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbtiHistory;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.service.MbtiDiagnosisDataQuartzService;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChildMbtiServiceImpl implements ChildMbtiService {

    private final ChildMbtiRepository childMbtiRepository;

    private ChildRepository childRepository;

    private MbtiRepository mbtiRepository;

    private ChildMbtiHistoryRepository childMbtiHistoryRepository;

    private ChildBookLikeRepository childBookLikeRepository;

    private ChildBookDisLikeRepository childBookDislikeRepository;

    private ChildFavorCategoryRepository childFavorCategoryRepository;

    private MbtiDiagnosisDataQuartzService mbtiDiagnosisDataQuartzService;

    private UserRepository userRepository;

    /**
     * 자녀의 MBTI 성향을 진단하고 저장하는 메서드 (ChildMbti와 ChildMbtiHistory에 저장).
     *
     * @param childId 자녀의 ID
     * @param scores  자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     */
    @Transactional
    public void createChildMbti(Long childId, MBTIScoresDto scores) {
        // Child 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("아이를 찾을 수 없습니다."));

        if (child.getCodeMbti() != null){
            // 기존 데이터 처리: ChildMbti와 ChildMbtiHistory의 deletedAt 필드 업데이트(논리적 삭제)
            updateDeletedAtForExistingRecords(child);
        }

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
    @Transactional
    public void updateDeletedAtForExistingRecords(Child child) {
        // 현재 시간 기준으로 30일 뒤 시간 설정
         LocalDateTime futureLocalDateTime = LocalDateTime.now().plusDays(30);
        // 현재 시간 기준으로 1분 뒤 시간 설정
//        LocalDateTime futureLocalDateTime = LocalDateTime.now().plusMinutes(1);
        child.setCodeMbti(null);

        // ChildMbti의 deletedAt 업데이트
        childMbtiRepository.updateDeletedAtForChild(child, futureLocalDateTime);

        // ChildMbtiHistory의 deletedAt 업데이트
        childMbtiHistoryRepository.updateDeletedAtForChild(child, futureLocalDateTime);

        // ChildBookLike의 deletedAt 업데이트
        childBookLikeRepository.updateDeletedAtForChild(child, futureLocalDateTime);

        // ChildBookDislike의 deletedAt 업데이트
        childBookDislikeRepository.updateDeletedAtForChild(child, futureLocalDateTime);

        // ChildFavorCategory의 deletedAt 업데이트
        childFavorCategoryRepository.updateDeletedAtForChild(child, futureLocalDateTime);

        childRepository.save(child);
        // 스케줄러를 이용하여 30일 뒤에 실제 삭제 수행
        try {
            mbtiDiagnosisDataQuartzService.
                    scheduleChildDeletion(child, futureLocalDateTime);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule child deletion", e);
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
        Mbti mbti = new Mbti();

        // MBTI 성향 업데이트 (점수에 따른 설정)
        int minusScore = -20;
        int plusScore = 20;
        mbti.setEiType(scores.getEiScore().equals("I") ? minusScore : plusScore); // I - / E +
        mbti.setSnType(scores.getSnScore().equals("S") ? minusScore : plusScore); // S - / N +
        mbti.setTfType(scores.getTfScore().equals("T") ? minusScore : plusScore); // T - / F +
        mbti.setJpType(scores.getJpScore().equals("J") ? minusScore : plusScore); // J - / P +

        // MBTI 저장
        mbtiRepository.save(mbti);

        ChildMbti childMbti = new ChildMbti();

        // 연관 관계 설정
        childMbti.setChild(child);
        childMbti.setMbti(mbti);

        child.setCodeMbti(calculateMBTI(scores));
        childMbtiRepository.save(childMbti);
        // ChildMbti 저장

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
        childMbtiHistory.setUpdatedAt(LocalDateTime.now());

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
        childMbtiHistory.setUpdatedAt(LocalDateTime.now());

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


    /**
     * 특정 아이의 정보와 아이의 MBTI 히스토리를 한 번에 조회하여 반환하는 메서드
     */
    @Transactional(readOnly = true)
    public ChildWithMbtiHistoryDto getChildMbtiHistory(Long childId) {
        // 아이의 정보 조회
        ChildDto childDto = childRepository.findChildDtoById(childId);

        // deletedAt이 null인 MBTI 히스토리 조회
        List<ChildMbtiHistoryDto> historyList = childMbtiHistoryRepository.findMbtiHistoryByChildId(childId);

        // 결과 반환
        return new ChildWithMbtiHistoryDto(childDto, historyList);
    }

    @Transactional
    public void softdelete(Long childId){
        // Child 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("아이를 찾을 수 없습니다."));

        updateDeletedAtForExistingRecords(child);
    }

    @Override
    public boolean existsChildMbti(Long childId){
        return childMbtiRepository.isDeletedByChildId(childId).orElse(false);
    }
    @Override
    public Optional<ChildMbtiDto> getChildMbtiInfo(Long childId) {
        return childRepository.findChildMbtiById(childId);
    }

    @Override
    public void navbarInfo(HttpSession session, Model model) {
        // 세션에서 사용자 이메일, 자녀 아이디를 가져옴
        String email = (String) session.getAttribute("userEmail");

        if (email != null) {  // 세션에 저장된 이메일이 있을 경우
            User user = userRepository.findByEmail(email);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }
    }
}

