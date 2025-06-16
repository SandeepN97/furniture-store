package com.example.furniturestore.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    private static class Attempt {
        int count = 0;
        Instant last = Instant.now();
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final int limit = 5;
    private final long windowMs = 10 * 60 * 1000; // 10 minutes

    public boolean isBlocked(String key) {
        Attempt a = attempts.get(key);
        if (a == null) return false;
        if (Instant.now().toEpochMilli() - a.last.toEpochMilli() > windowMs) {
            attempts.remove(key);
            return false;
        }
        return a.count >= limit;
    }

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    public void loginFailed(String key) {
        Attempt a = attempts.computeIfAbsent(key, k -> new Attempt());
        if (Instant.now().toEpochMilli() - a.last.toEpochMilli() > windowMs) {
            a.count = 0;
        }
        a.count++;
        a.last = Instant.now();
    }
}
