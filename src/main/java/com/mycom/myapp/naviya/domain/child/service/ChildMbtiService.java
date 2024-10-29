package com.mycom.myapp.naviya.domain.child.service;

import com.mycom.myapp.naviya.domain.child.dto.ChildWithMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.dto.MBTIScoresDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;

public interface ChildMbtiService {

    /**
     * 자녀의 MBTI 성향을 진단하고 저장하는 메서드 (ChildMbti 엔티티에 저장).
     *
     * @param childId 자녀의 ID (Long 형식)
     * @param scores  자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     *
     * 예외:
     * @throws RuntimeException 자녀를 찾을 수 없거나, 저장 과정에서 오류가 발생할 경우 예외 발생
     */
    void createChildMbti(Long childId, MBTIScoresDto scores);

    /**
     * 자녀의 MBTI 진단 정보를 바탕으로 ChildMbtiHistory에 히스토리를 생성하는 메서드.
     *
     * @param child  자녀 엔티티 객체
     * @param scores 자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     *
     * 예외:
     * @throws RuntimeException 자녀 또는 MBTI 데이터에 문제가 있을 경우 예외 발생
     */
    void createChildMbtiHistory(Child child, MBTIScoresDto scores);

    /**
     * MBTI 테이블에 저장된 데이터를 기반으로 자녀의 MBTI 히스토리를 생성하고 자녀의 MBTI 정보를 업데이트하는 메서드.
     *
     * @param child 자녀 엔티티 객체
     *
     * 예외:
     * @throws RuntimeException 자녀 또는 MBTI 데이터를 찾을 수 없을 경우 예외 발생
     */
    void createChildMbtiHistory(Child child);

    /**
     * 자녀의 MBTI 점수 데이터를 바탕으로 MBTI 결과를 생성하는 메서드.
     *
     * @param scores 자녀의 MBTI 점수 데이터를 담은 MBTIScoresDto 객체
     * @return MBTI 결과 문자열 (예: "ISTP", "ENFP" 등)
     */
    String calculateMBTI(MBTIScoresDto scores);

    /**
     * MBTI 테이블에 저장된 데이터를 기반으로 MBTI 결과를 생성하는 메서드.
     *
     * @param mbti Mbti 엔티티 객체 (MBTI 관련 데이터가 저장된 엔티티)
     * @return MBTI 결과 문자열 (예: "ISTP", "ENFP" 등)
     */
    String calculateMBTI(Mbti mbti);

    /**
     * 자녀의 MBTI 히스토리 조회 메서드
     *
     * @param childId 자녀의 ID
     * @return 자녀의 MBTI 변경 이력 리스트
     */

    ChildWithMbtiHistoryDto getChildMbtiHistory(Long childId);

    /**
     * 자녀의 MBTI 히스토리 삭제 메서드
     *
     * @param childId 자녀의 ID
     * @return 자녀의 MBTI 변경 이력 리스트
     */
    void softdelete(Long childId);

    boolean existsChildMbti(Long childId);

}
