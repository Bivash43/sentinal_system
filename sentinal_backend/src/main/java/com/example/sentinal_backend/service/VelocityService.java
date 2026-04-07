package com.example.sentinal_backend.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VelocityService {

    private final StringRedisTemplate redisTemplate;

    @Value("${velocity.limit.transaction}")
    private int LIMIT;

    @Value("${velocity.limit.window.seconds}")
    private long WINDOW_SECONDS;

    @CircuitBreaker(name = "redis-velocity-check", fallbackMethod = "bypassVelocityCheck")
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

    public boolean bypassVelocityCheck(String cardNumber, Exception e) {
        log.warn("Redis Circuit Breaker OPEN! Bypassing velocity checks for card {}. Error: {}", cardNumber, e.getMessage());
        return false;
    }
}