package com.qrrestaurant.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisSessionService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SESSION_PREFIX = "session:";
    private static final String USER_ACTIVITY_PREFIX = "user_activity:";
    private static final Duration SESSION_TIMEOUT = Duration.ofHours(24);

    public void createSession(String sessionId, String username) {
        String sessionKey = SESSION_PREFIX + sessionId;
        String userActivityKey = USER_ACTIVITY_PREFIX + username;
        
        redisTemplate.opsForValue().set(sessionKey, username, SESSION_TIMEOUT);
        redisTemplate.opsForSet().add(userActivityKey, sessionId);
        redisTemplate.expire(userActivityKey, SESSION_TIMEOUT);
    }

    public String getUsernameFromSession(String sessionId) {
        return (String) redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
    }

    public void invalidateSession(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        String username = getUsernameFromSession(sessionId);
        
        if (username != null) {
            String userActivityKey = USER_ACTIVITY_PREFIX + username;
            redisTemplate.opsForSet().remove(userActivityKey, sessionId);
        }
        
        redisTemplate.delete(sessionKey);
    }

    public Set<Object> getUserActiveSessions(String username) {
        return redisTemplate.opsForSet().members(USER_ACTIVITY_PREFIX + username);
    }

    public void updateSessionActivity(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.expire(sessionKey, SESSION_TIMEOUT);
    }
} 