package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.dto.ChildMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbtiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ChildMbtiHistoryRepository extends JpaRepository<ChildMbtiHistory, Long> {

    // deletedAt이 null인 ChildMbtiHistory를 조회
    @Modifying
    @Transactional
    @Query("UPDATE ChildMbtiHistory cmh SET cmh.deletedAt = :futureDate WHERE cmh.child = :child AND cmh.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    // 특정 아이의 MBTI 히스토리에서 deletedAt이 null인 경우만 조회, updatedAt을 기준으로 최근 값이 먼저 나오도록 정렬
    @Query("SELECT new com.mycom.myapp.naviya.domain.child.dto.ChildMbtiHistoryDto(h.mbtiHistoryId, h.updatedAt, h.codeNewMbti) " +
            "FROM ChildMbtiHistory h " +
            "WHERE h.child.childId = :childId AND h.deletedAt IS NULL " +
            "ORDER BY h.updatedAt DESC")
    List<ChildMbtiHistoryDto> findMbtiHistoryByChildId(Long childId);


    @Transactional
    @Modifying
    @Query("DELETE FROM ChildMbtiHistory h WHERE h.child.childId = :childId AND h.deletedAt = :deleteAt")
    void deleteByChildIdAndDeletedAt(Long childId, LocalDateTime deleteAt);

}
