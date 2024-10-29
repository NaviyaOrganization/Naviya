package com.mycom.myapp.naviya.global.redies;

import io.lettuce.core.RedisBusyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
            // Check if the stream exists; if it doesn't, create it
            if (!redisTemplate.opsForStream().size(STREAM_KEY).equals(0L)) {
                // Check if the consumer group exists
                try {
                    redisTemplate.opsForStream().createGroup(STREAM_KEY, GROUP_NAME);
                } catch (RedisBusyException e) {
                    // Group already exists; handle this gracefully
                    System.out.println("Consumer group already exists: " + GROUP_NAME);
                }
            }
        } catch (Exception e) {
            // Handle exception
            System.err.println("Error initializing Redis stream: " + e.getMessage());
        }
    }
}
