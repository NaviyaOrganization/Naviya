package com.mycom.myapp.naviya.domain.lottery.repository;

import com.mycom.myapp.naviya.domain.lottery.entity.LotteryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotteryRepository extends JpaRepository<LotteryEntry, Long> {
    Optional<LotteryEntry> findByPhone(String phone); // 이메일 기반으로 중복 체크

//    UPDATE lottery_entries
//    SET status = :status
//    WHERE id IN (
//            SELECT id
//    FROM lottery_entries
//            WHERE status = 'pending'
//            ORDER BY created_at
//            LIMIT :limit
//    )
    @Modifying
    @Query(value = "UPDATE lottery_entries SET status = :status WHERE id IN (SELECT id FROM lottery_entries WHERE status = 'pending' ORDER BY created_at LIMIT :limit)", nativeQuery = true)
    void updateStatusForWinners(@Param("status") String status, @Param("limit") int limit);

    @Modifying
    @Query(value = "UPDATE lottery_entries SET status = 'fail' WHERE status = 'pending'", nativeQuery = true)
    void updateStatusForLosers();
}