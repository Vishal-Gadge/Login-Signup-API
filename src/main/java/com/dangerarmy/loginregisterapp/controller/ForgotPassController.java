package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.service.MyAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class ForgotPassController {

    @Autowired
    private MyAppUserService myAppUserService;

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    @PostMapping("/req/forgotPass")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody UserModel userModel){
        System.out.println("Forgot password controller hit");
        try{
            myAppUserService.updatePassword(userModel);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(404).body(Map.of("error","user not found"));
        }catch (BadCredentialsException e){
            return ResponseEntity.status(400).body(Map.of("error","Username does not match email"));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(429).body(Map.of("error", "Too many failure requests, try again later"));
        }
        return ResponseEntity.ok(Map.of("message","Password successfully changed"));
    }
}
