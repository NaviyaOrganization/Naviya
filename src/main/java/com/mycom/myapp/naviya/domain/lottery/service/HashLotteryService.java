package com.mycom.myapp.naviya.domain.lottery.service;

import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;
import com.mycom.myapp.naviya.domain.lottery.entity.LotteryEntry;
import com.mycom.myapp.naviya.domain.lottery.repository.LotteryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class HashLotteryService implements LotteryService {

    private final LotteryRepository lotteryRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String LOTTERY_HASH_KEY = "lottery:entries";
    private static final String LOTTERY_COUNT_KEY = "lottery:count";
    private static final String LOTTERY_WINNERS_LIST = "lottery:winners";

    // 캐시 변수: 마스킹된 응모 데이터를 저장
    private List<String> cachedMaskedEntries = new CopyOnWriteArrayList<>();

    @Override
    @Transactional
    public String submitLotteryEntry(LotteryEntryRequest request) {

        try {
            String phone = request.getPhone();
            String name = request.getName();
            String entryKey = phone;
            String entryData = name + ":" + phone;

            log.info("새로운 응모 요청 처리 중 - 이름: {}, 전화번호: {}", name, phone);

            // Redis에서 중복 체크 (LOTTERY_WINNERS_LIST 안에 전화번호가 있는지 확인)
            Boolean isDuplicate = redisTemplate.opsForHash().hasKey(LOTTERY_HASH_KEY, entryKey);
            if (Boolean.TRUE.equals(isDuplicate)) {
                log.warn("중복 응모 시도 - 전화번호: {}", phone);
                return "이미 응모한 전화번호입니다.";
            }

            // 응모 카운트 확인
            Long currentCount = redisTemplate.opsForValue().increment(LOTTERY_COUNT_KEY, 1);

            if (currentCount > 100) {
                redisTemplate.opsForValue().decrement(LOTTERY_COUNT_KEY); // 카운트 복구
                log.info("응모 마감 - 현재 응모자 수가 100명을 초과하였습니다.");
                return "응모가 마감되었습니다.";
            }

            // 성공한 응모 처리: 이름과 전화번호를 Redis 리스트에 추가
            redisTemplate.opsForHash().put(LOTTERY_HASH_KEY, entryKey, entryData);

            log.info("응모가 접수되었습니다 - 이름: {}, 전화번호: {}", name, phone);
            return "응모가 접수되었습니다. 결과는 내일 오후 1시에 발표됩니다.";

        } catch (Exception e) {
            log.error("응모 처리 중 오류 발생 - 이름: {}, 전화번호: {} 오류{}", request.getPhone(), request.getPhone(), e.getMessage());
            return "응모 처리 중 오류가 발생했습니다. 다시 시도해주세요.";
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 02 10 * * ?")
    public void processLotteryResults() {

        log.info("응모 결과 처리 시작.");

        // Redis에서 성공한 100명 응모자 정보 가져오기
        List<Object> winnersList = redisTemplate.opsForHash().values((LOTTERY_HASH_KEY));
        if (winnersList.isEmpty()){
            log.info("응모자 정보가 없습니다.");
            return;
        }

        for (Object entryData : winnersList) {
            String[] parts = entryData.toString().split(":");
            if (parts.length != 2) {
                log.warn("잘못된 응모 데이터 형식: {}", entryData);
                continue;
            }
            String name = parts[0];
            String phone = parts[1];

            LotteryEntry entry = new LotteryEntry(name, phone);
            lotteryRepository.save(entry);
        }

        // Redis에서 응모 데이터 삭제
        redisTemplate.delete(LOTTERY_HASH_KEY);
        redisTemplate.delete(LOTTERY_COUNT_KEY);

        log.info("응모 데이터가 MySQL로 이전되었습니다. 이벤트가 종료되었습니다.");
    }

    // 매일 오후 1시에 캐시 업데이트
    // @Scheduled(cron = "0 0 13 * * ?") // 매일 오후 1시에 실행
    @Scheduled(cron = "0 57 15 * * ?") // 매일 오후 3시 36분에 실행
    public void updateMaskedEntriesCache() {
        log.info("어제 오후 1시부터 오늘 오후 1시까지의 데이터를 캐시로 업데이트 중...");

        // 어제 오후 1시부터 오늘 오후 1시까지의 시간 범위 설정
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.minusDays(1).atTime(13, 0); // 어제 오후 1시
        LocalDateTime endTime = today.atTime(13, 0); // 오늘 오후 1시

        // 해당 시간 범위의 데이터를 조회
        List<LotteryEntry> entries = lotteryRepository.findAllByCreatedAtBetween(startTime, endTime);

        // 마스킹 처리된 데이터를 캐시에 저장
        cachedMaskedEntries = entries.stream()
                .map(entry -> entry.getName() + ": " + maskPhone(entry.getPhone()))
                .collect(Collectors.toList());

        log.info("캐시가 업데이트되었습니다. 총 {}개의 항목이 캐시에 저장되었습니다.", cachedMaskedEntries.size());
    }

    // 캐시된 마스킹된 응모 데이터를 반환 (캐시가 없으면 즉시 업데이트)
    public List<String> getCachedMaskedEntries() {
        // 캐시에 값이 없는 경우 실시간으로 업데이트
        if (cachedMaskedEntries.isEmpty()) {
            log.info("캐시에 데이터가 없어서 즉시 업데이트를 수행합니다.");
            updateMaskedEntriesCache();
        }
        return new ArrayList<>(cachedMaskedEntries);
    }

    // 전화번호 마스킹 처리 함수
    private String maskPhone(String phone) {
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 2);
    }
}
