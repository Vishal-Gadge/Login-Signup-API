package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public void rateLimiter(String key,int chances,Duration ttl){
        Long attempts = redisTemplate.opsForValue().increment(key);
        if(attempts == 1){
            redisTemplate.expire(key,ttl);
        }
        if(attempts > chances){
            log.error("{} has crossed their {} chances in {}",key, chances, ttl);
            throw new RateLimitExceededException("Too many failure requests, Slow down bro ⚆_⚆");
        }
    }
}
