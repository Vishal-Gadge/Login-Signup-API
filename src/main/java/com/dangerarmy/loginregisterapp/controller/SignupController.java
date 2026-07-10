package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.dto.SignupRequest;
import com.dangerarmy.loginregisterapp.service.SignupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/req/signup/save")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignupRequest user) {
        signupService.signup(user);
        return ResponseEntity.ok(Map.of("message",
                "If an account exists with this email, you will receive instructions, check mail"));
    }
}