package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.dto.ForgotPassRequest;
import com.dangerarmy.loginregisterapp.dto.UserRequest;
import com.dangerarmy.loginregisterapp.service.ForgotPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ForgotPassController {

    @Autowired
    private ForgotPassService forgotPassService;

    @PostMapping("/req/forgotPass")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody UserRequest userRequest){
        forgotPassService.forgotPassword(userRequest);
        return ResponseEntity.ok(Map.of(
            "message","OTP has been send to given email, Consider entering the OTP to change password"));
    }

    @PostMapping("/verify/forgotPass")
    public ResponseEntity<Map<String,String>> verifyForgotPass(@RequestBody ForgotPassRequest request){
        forgotPassService.verifyForgotPass(request.getOtp());
        return ResponseEntity.ok(Map.of(
                "message","Password has been Successfully changed"));
    }
}
