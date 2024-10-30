package com.mycom.myapp.naviya.global.redies;

import io.lettuce.core.RedisBusyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RedisStreamInitializer {

    private static final String STREAM_KEY = "likeDislikeStream"; // 스트림 키
    private static final String GROUP_NAME = "likeDislikeGroup"; // 그룹 이름

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisStreamInitializer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        init();
    }

    public void init() {
        try {
            // 스트림이 존재하지 않으면 빈 스트림 생성
            if (redisTemplate.opsForStream().size(STREAM_KEY) == null) {
                redisTemplate.opsForStream().add(STREAM_KEY, Collections.singletonMap("initial", "value"));
            }

            // 컨슈머 그룹이 없다면 생성
            try {
                redisTemplate.opsForStream().createGroup(STREAM_KEY, GROUP_NAME);
                System.out.println("Consumer group created: " + GROUP_NAME);
            } catch (RedisBusyException e) {
                System.out.println("Consumer group already exists: " + GROUP_NAME);
            }
        } catch (Exception e) {
            // 예외 처리
            System.err.println("Error initializing Redis stream: " + e.getMessage());
        }
    }
}
