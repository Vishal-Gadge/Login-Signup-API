package com.dangerarmy.loginregisterapp.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutService {

    private final JwtService jwtService;
    private final EmailService emailService;

    public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        String email = null;
        if(servletRequest.getCookies() != null){
            for(Cookie cookie : servletRequest.getCookies()){
                if("jwt".equals(cookie.getName())){
                    email = jwtService.getUsername(jwtService.extractClaims(cookie.getValue()));
                    break;
                }
            }
        }

        ResponseCookie logoutCookie = ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE , logoutCookie.toString());
        log.info("User with email :{} has been logout",emailService.maskEmail(email));
    }
}
