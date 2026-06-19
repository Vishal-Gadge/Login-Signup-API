package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.dto.EmailRequest;
import com.dangerarmy.loginregisterapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/verify/email")
    public ResponseEntity<Map<String,String>> verifyEmail(@RequestParam String token){
        //betrayed line
        //System.out.println(emailService.verifyEmail(token));
        emailService.verifyEmail(token);
        return ResponseEntity.ok()
                .body(Map.of("message","Email has been verified"));
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?> resendEmail(@RequestBody EmailRequest req){
        emailService.isValidEmail(req.getEmail());

        emailService.resendEmail(req.getEmail());
        return ResponseEntity.ok(Map.of("message","Email Resend Successfully"));
    }
}
