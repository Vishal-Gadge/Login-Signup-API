package com.dangerarmy.loginregisterapp.controller;

import java.util.Map;

import com.dangerarmy.loginregisterapp.dto.UserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dangerarmy.loginregisterapp.service.LoginService;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/req/login/verify")
    public ResponseEntity<?> verifyUserLogin(@RequestBody UserRequest user, HttpServletRequest request,HttpServletResponse response){
        loginService.login(user, request, response);
        return ResponseEntity.ok(Map.of("message","Login successful"));
    }
}
