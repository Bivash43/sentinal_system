package com.example.sentinal_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VelocityServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private VelocityService velocityService;

    @BeforeEach
    void setUp() {
        velocityService = new VelocityService(redisTemplate);
        ReflectionTestUtils.setField(velocityService, "LIMIT", 5);
        ReflectionTestUtils.setField(velocityService, "WINDOW_SECONDS", 60L);
    }

    @Test
    void shouldReturnTrueWhenIncrementedValueExceedsLimit() {
        String cardNumber = "4111111111111111";
        String key = "velocity:" + cardNumber;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(6L);

        boolean result = velocityService.isVelocityExceeded(cardNumber);

        assertTrue(result);
        verify(valueOperations).increment(key);
    }
}
