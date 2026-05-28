package com.dangerarmy.loginregisterapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    @GetMapping("/")
    public String redisCheck(){
        redisTemplate.opsForValue().set("trademark","DangerArmy");
        return redisTemplate.opsForValue().get("trademark");
    }
}
