package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.exception.ExpiredTokenException;
import com.dangerarmy.loginregisterapp.exception.InvalidTokenException;
import com.dangerarmy.loginregisterapp.exception.UserAlreadyVerifiedException;
import com.dangerarmy.loginregisterapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VerifyEmailController {

    private final EmailService emailService;

    @PostMapping("/verify/email")
    public ResponseEntity<Map<String,String>> verifyEmail(@RequestParam String token){
        try {
            //betrayed line
            //System.out.println(emailService.verifyEmail(token));
            emailService.verifyEmail(token);
            return ResponseEntity.ok()
                    .body(Map.of("message","Email has been verified"));
        } catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message",e.getMessage()));
        }catch (UserAlreadyVerifiedException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",e.getMessage()));
        }catch (ExpiredTokenException e){
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of("message",e.getMessage()));
        }
    }
}
