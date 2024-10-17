package com.mycom.myapp.naviya.domain.lottery.service;

import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;
import com.mycom.myapp.naviya.domain.lottery.entity.LotteryEntry;
import com.mycom.myapp.naviya.domain.lottery.repository.LotteryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LotterytServiceImpl implements LotteryService{

    private final LotteryRepository lotteryRepository;
    private final StringRedisTemplate redisTemplate;


    @Override
    public String submitLotteryEntry(LotteryEntryRequest request) {

        try {
            log.info("새로운 응모 요청 처리 중 - 이메일: {}", request.getEmail());

            if (Boolean.TRUE.equals(redisTemplate.hasKey(request.getEmail()))) {
                log.warn("중복 응모 시도 - 이메일: {}", request.getEmail());
                return "이미 응모한 이메일입니다.";
            }

            Optional<LotteryEntry> existingEntry = lotteryRepository.findByEmail(request.getEmail());
            if (existingEntry.isPresent()) {
                log.warn("DB 중복 응모 시도 - 이메일: {}", request.getEmail());
                return "이미 응모한 이메일입니다.";
            }

            LotteryEntry newEntry = new LotteryEntry(request.getName(), request.getEmail());
            lotteryRepository.save(newEntry);
            redisTemplate.opsForValue().set(request.getEmail(), "submitted", 24, TimeUnit.HOURS);

            return "응모가 접수되었습니다. 결과는 내일 오후 1시에 발표됩니다.";
        } catch (Exception e) {
            log.error("응모 처리 중 오류 발생 - 이메일: {}, 오류: {}", request.getEmail(), e.getMessage());
            return "응모 처리 중 오류가 발생했습니다. 다시 시도해주세요.";
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 13 * * *")
    public void processLotteryResults() {
        // 100명 선발
        lotteryRepository.updateStatusForWinners("success", 100);
        // 나머지 실패 처리
        lotteryRepository.updateStatusForLosers();
    }
}
