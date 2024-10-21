package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.dto.ChildMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbtiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChildMbtiHistoryRepository extends JpaRepository<ChildMbtiHistory, Long> {

    // deletedAt이 null인 ChildMbtiHistory를 조회
    @Query("SELECT cmh FROM ChildMbtiHistory cmh WHERE cmh.child = :child AND cmh.deletedAt IS NULL")
    List<ChildMbtiHistory> findByChildAndDeletedAtIsNull(@Param("child") Child child);

    // 특정 아이의 MBTI 히스토리만 조회
    @Query("SELECT new com.mycom.myapp.naviya.domain.child.dto.ChildMbtiHistoryDto(h.mbtiHistoryId, h.updatedAt, h.codeNewMbti) " +
            "FROM ChildMbtiHistory h " +
            "WHERE h.child.childId = :childId")
    List<ChildMbtiHistoryDto> findMbtiHistoryByChildId(Long childId);

}
