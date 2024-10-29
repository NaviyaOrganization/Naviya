package com.mycom.myapp.naviya.domain.lottery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;
import com.mycom.myapp.naviya.domain.lottery.entity.LotteryEntry;
import com.mycom.myapp.naviya.domain.lottery.repository.LotteryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
//@Primary
public class LuaAndQueueLotteryService implements LotteryService {

    private final LotteryRepository lotteryRepository;
    private final StringRedisTemplate redisTemplate;
    private final ReactiveStringRedisTemplate reactiveRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String LOTTERY_HASH_KEY = "lottery:entries";
    private static final String LOTTERY_COUNT_KEY = "lottery:count";
    private static final String DUPLICATE_RESPONSE_KEY = "duplicate_response";
    private static final String ENTRY_PROCESSED_RESPONSE_KEY = "entry_processed_response";
    private static final String LOTTERY_STREAM_KEY = "lottery:stream";

    private static final String DUPLICATE_RESPONSE_MESSAGE = "이미 응모한 전화번호입니다.";
    private static final String ENTRY_PROCESSED_RESPONSE_MESSAGE = "응모가 접수되었습니다. 결과는 내일 오후 1시에 발표됩니다.";

    // 서비스 초기화 시 메시지 캐시 설정
    @PostConstruct
    public void initCacheMessages() {
        redisTemplate.opsForValue().setIfAbsent(DUPLICATE_RESPONSE_KEY, DUPLICATE_RESPONSE_MESSAGE);
        redisTemplate.opsForValue().setIfAbsent(ENTRY_PROCESSED_RESPONSE_KEY, ENTRY_PROCESSED_RESPONSE_MESSAGE);
    }

    @Override
    @Transactional
    public String submitLotteryEntry(LotteryEntryRequest request) {
        String script =
                "local count_key = KEYS[1] " +
                        "local hash_key = KEYS[2] " +
                        "local entry_key = ARGV[1] " +
                        "local entry_data = ARGV[2] " +
                        "local max_entries = 100 " + // 최대 100명 제한
                        "local current_count = tonumber(redis.call('GET', count_key) or '0') " +

                        // 중복 체크
                        "if redis.call('HEXISTS', hash_key, entry_key) == 1 then " +
                        "   return redis.call('GET', KEYS[3]) " +  // 중복 응모 메시지 반환
                        "end " +

                        // 100명 제한 체크
                        "if current_count >= max_entries then " +
                        "   return '응모가 마감되었습니다.' " + // 선착순 마감 메시지 반환
                        "end " +

                        // 응모 데이터 저장 및 카운트 증가
                        "redis.call('HSET', hash_key, entry_key, entry_data) " +
                        "redis.call('INCR', count_key) " +
                        "return redis.call('GET', KEYS[4])";       // 응모 완료 메시지 반환

        String entryKey = request.getPhone();
        String entryData = String.format("{\"name\":\"%s\", \"phone\":\"%s\"}", request.getName(), request.getPhone());

        try {
            byte[] resultBytes = redisTemplate.execute((RedisCallback<byte[]>) connection ->
                    connection.scriptingCommands().eval(
                            script.getBytes(),
                            ReturnType.VALUE,
                            4,
                            LOTTERY_COUNT_KEY.getBytes(),
                            LOTTERY_HASH_KEY.getBytes(),
                            DUPLICATE_RESPONSE_KEY.getBytes(),
                            ENTRY_PROCESSED_RESPONSE_KEY.getBytes(),
                            entryKey.getBytes(),
                            entryData.getBytes()
                    )
            );

            String result = resultBytes != null ? new String(resultBytes) : "오류 발생";
            log.info("응모 처리 결과: {}", result);

            // 비동기로 Redis Streams에 데이터 기록
            writeToLotteryStream(request.getName(), request.getPhone());

            return result;

        } catch (Exception e) {
            log.error("응모 처리 중 오류 발생 - 이름: {}, 전화번호: {} 오류{}", request.getName(), request.getPhone(), e.getMessage());
            return "응모 처리 중 오류가 발생했습니다. 다시 시도해주세요.";
        }
    }

    // Redis Streams에 데이터 쓰기
    private void writeToLotteryStream(String name, String phone) {
        MapRecord<String, String, String> record = MapRecord.create(LOTTERY_STREAM_KEY, Map.of("name", name, "phone", phone));
        reactiveRedisTemplate.opsForStream()
                .add(record)
                .doOnSuccess(recordId -> log.info("Stream에 추가된 응모 ID: {}", recordId.getValue()))
                .subscribe();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 55 12 * * ?")
    public void processLotteryResults() {
        log.info("응모 결과 처리 시작.");

        List<Object> winnersList = redisTemplate.opsForHash().values(LOTTERY_HASH_KEY);
        if (winnersList.isEmpty()) {
            log.info("응모자 정보가 없습니다.");
            return;
        }

        for (Object entryData : winnersList) {
            try {
                // JSON 데이터를 파싱하여 name과 phone 필드를 추출
                Map<String, String> data = objectMapper.readValue(entryData.toString(), Map.class);
                String name = data.get("name");
                String phone = data.get("phone");

                // 추출한 데이터를 사용하여 LotteryEntry 생성 및 저장
                LotteryEntry entry = new LotteryEntry(name, phone);
                lotteryRepository.save(entry);
            } catch (Exception e) {
                log.warn("응모 데이터 파싱 오류 - 잘못된 응모 데이터 형식: {}, 오류: {}", entryData, e.getMessage());
            }
        }

        // Redis에서 응모 데이터 삭제
        redisTemplate.delete(LOTTERY_HASH_KEY);
        redisTemplate.delete(LOTTERY_COUNT_KEY);
        redisTemplate.delete(LOTTERY_STREAM_KEY);

        log.info("응모 데이터가 MySQL로 이전되었습니다. 이벤트가 종료되었습니다.");
    }

    @Scheduled(cron = "0 00 13 * * ?") // 매일 오후 1시에 실행
    public void updateMaskedEntriesCache() {
        log.info("어제 오후 1시부터 오늘 오후 1시까지의 데이터를 캐시로 업데이트 중...");

        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.minusDays(1).atTime(13, 0);
        LocalDateTime endTime = today.atTime(13, 0);

        List<LotteryEntry> entries = lotteryRepository.findAllByCreatedAtBetween(startTime, endTime);

        cachedMaskedEntries = entries.stream()
                .map(entry -> entry.getName() + ": " + maskPhone(entry.getPhone()))
                .collect(Collectors.toList());

        log.info("캐시가 업데이트되었습니다. 총 {}개의 항목이 캐시에 저장되었습니다.", cachedMaskedEntries.size());
    }

    private List<String> cachedMaskedEntries = new CopyOnWriteArrayList<>();

    public List<String> getCachedMaskedEntries() {
        if (cachedMaskedEntries.isEmpty()) {
            log.info("캐시에 데이터가 없어서 즉시 업데이트를 수행합니다.");
            updateMaskedEntriesCache();
        }
        return new ArrayList<>(cachedMaskedEntries);
    }

    private String maskPhone(String phone) {
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 2);
    }
}
