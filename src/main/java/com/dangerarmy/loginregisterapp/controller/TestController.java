package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @GetMapping("/")
    public String Check(HttpServletResponse httpResponse){
//        redisTemplate.opsForValue().set("trademark","DangerArmy");
//        return redisTemplate.opsForValue().get("trademark");
//        List<String> roles = userRolesRepo.getRoles(1);
//        System.out.println("Roles are :"+roles);
//        for(String role : roles){
//            System.out.println("Role is: "+role);
//        }
//        return roles.toString();
        System.out.println(httpResponse.getHeaderNames());
        System.out.println(httpResponse.getHeader(HttpHeaders.SET_COOKIE));
        return null;
    }
}
