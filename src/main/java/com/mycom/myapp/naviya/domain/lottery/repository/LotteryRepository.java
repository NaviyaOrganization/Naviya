package com.mycom.myapp.naviya.domain.lottery.repository;

import com.mycom.myapp.naviya.domain.lottery.entity.LotteryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotteryRepository extends JpaRepository<LotteryEntry, Long> {
    List<LotteryEntry> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}