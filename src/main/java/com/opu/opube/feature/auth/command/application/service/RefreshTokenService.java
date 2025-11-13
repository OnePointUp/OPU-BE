package com.opu.opube.feature.auth.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "RT:";

    public void save(Long memberId, String refreshToken, long expiresInSeconds) {
        String key = KEY_PREFIX + memberId;
        redisTemplate.opsForValue()
                .set(key, refreshToken, Duration.ofSeconds(expiresInSeconds));
    }

    public String get(Long memberId) {
        String key = KEY_PREFIX + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(Long memberId) {
        String key = KEY_PREFIX + memberId;
        redisTemplate.delete(key);
    }
}