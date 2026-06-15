package com.dangerarmy.loginregisterapp.controller;

import java.time.Duration;
import java.util.Map;

import com.dangerarmy.loginregisterapp.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.service.JwtService;
import com.dangerarmy.loginregisterapp.service.LoginService;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/req/login/verify")
    public ResponseEntity<?> verifyUserLogin(@RequestBody LoginRequest req, HttpServletResponse response){
        UserModel dbUser = loginService.verifyAndGetUser(req.getEmail(), req.getPassword());

        if(dbUser == null){
            return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        }

        if(!dbUser.isVerified()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error","Email is not verified"));
        }

        String jwt = jwtService.generateToken(dbUser);
        System.out.println("Token is : "+jwt);
        ResponseCookie cookie = ResponseCookie.from("jwt" , jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message","Login successful"));
    }
}
