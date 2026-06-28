package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public void rateLimiter(String key,int chances,Duration ttl){
        Long attempts = redisTemplate.opsForValue().increment(key);
        if(attempts == 1){
            redisTemplate.expire(key,ttl);
        }
        if(attempts > chances){
            throw new RateLimitExceededException("Too many failure requests, Slow down bro ⚆_⚆");
        }
    }
}
