package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChildMbtiRepository extends JpaRepository<ChildMbti, Long> {
    // child_id로 ChildMbti 엔티티를 찾기 위한 메서드
    ChildMbti findByChild_ChildId(Long childId);

    // Child와 연관된 ChildMbti와 Mbti를 함께 조회하는 JPQL 쿼리
    @Query("SELECT cm FROM ChildMbti cm JOIN FETCH cm.mbti WHERE cm.child = :child")
    Optional<ChildMbti> findByChildWithMbti(@Param("child") Child child);

    // deletedAt이 null인 ChildMbti만 조회 (Mbti와 JOIN 없이)
    @Modifying
    @Transactional
    @Query("UPDATE ChildMbti cm SET cm.deletedAt = :futureDate WHERE cm.child = :child AND cm.deletedAt IS NULL")
    void updateDeletedAtForChild(@Param("child") Child child, @Param("futureDate") LocalDateTime futureDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM ChildMbti c WHERE c.child.childId = :childId AND c.deletedAt = :deleteAt")
    void deleteByChildIdAndDeletedAt(Long childId, LocalDateTime deleteAt);



}