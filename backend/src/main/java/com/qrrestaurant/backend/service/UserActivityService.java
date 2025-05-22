package com.qrrestaurant.backend.service;

import java.time.Duration;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserActivityService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String USER_LOGIN_HISTORY = "user_login_history:";
    private static final String USER_LAST_ACTIVITY = "user_last_activity:";
    private static final int MAX_LOGIN_HISTORY = 10;
    private static final Duration ACTIVITY_TIMEOUT = Duration.ofDays(30);

    public void recordLogin(String username, String ipAddress) {
        String loginHistoryKey = USER_LOGIN_HISTORY + username;
        String lastActivityKey = USER_LAST_ACTIVITY + username;
        
        Map<String, String> loginInfo = Map.of(
            "timestamp", String.valueOf(Instant.now().toEpochMilli()),
            "ip", ipAddress
        );
        
        redisTemplate.opsForList().leftPush(loginHistoryKey, loginInfo);
        redisTemplate.opsForList().trim(loginHistoryKey, 0, MAX_LOGIN_HISTORY - 1);
        redisTemplate.expire(loginHistoryKey, ACTIVITY_TIMEOUT);
        
        redisTemplate.opsForValue().set(lastActivityKey, Instant.now().toEpochMilli());
        redisTemplate.expire(lastActivityKey, ACTIVITY_TIMEOUT);
    }

    public List<Object> getLoginHistory(String username) {
        return redisTemplate.opsForList().range(USER_LOGIN_HISTORY + username, 0, -1);
    }

    public void updateLastActivity(String username) {
        String lastActivityKey = USER_LAST_ACTIVITY + username;
        redisTemplate.opsForValue().set(lastActivityKey, Instant.now().toEpochMilli());
        redisTemplate.expire(lastActivityKey, ACTIVITY_TIMEOUT);
    }

    public Long getLastActivityTime(String username) {
        return (Long) redisTemplate.opsForValue().get(USER_LAST_ACTIVITY + username);
    }
} 