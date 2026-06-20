package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.exception.ExpiredEmailException;
import com.dangerarmy.loginregisterapp.exception.InvalidEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public int rateLimiter(String key,Duration ttl){
        String attemptStr = redisTemplate.opsForValue().get(key);
        int attempts;
        if(attemptStr == null){
            redisTemplate.expire(key,ttl);
            attempts = 0;
        }else{
            attempts = Integer.parseInt(attemptStr);
        }
        redisTemplate.opsForValue().increment(key);
        if(attempts >= 3){
            throw new ExpiredEmailException("Too many failure requests, Try again later");
        }
        return attempts;
    }
}
