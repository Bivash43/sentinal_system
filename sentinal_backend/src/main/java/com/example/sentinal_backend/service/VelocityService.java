package com.example.sentinal_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VelocityService {

    private final StringRedisTemplate redisTemplate;

    @Value("${velocity.limit.transaction}")
    private int LIMIT;

    @Value("${velocity.limit.window.seconds}")
    private long WINDOW_SECONDS;

    public boolean isVelocityExceeded(String cardNumber) {
        String key = "velocity:" + cardNumber;

        // Increment the count for this card
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount != null && currentCount == 1) {
            // First request in the window, set expiration
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        return currentCount != null && currentCount > LIMIT;
    }
}