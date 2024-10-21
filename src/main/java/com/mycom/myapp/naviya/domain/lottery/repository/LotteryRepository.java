package com.mycom.myapp.naviya.domain.lottery.repository;

import com.mycom.myapp.naviya.domain.lottery.entity.LotteryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LotteryRepository extends JpaRepository<LotteryEntry, Long> {
    List<LotteryEntry> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}