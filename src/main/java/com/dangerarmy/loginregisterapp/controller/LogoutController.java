package com.dangerarmy.loginregisterapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class LogoutController {

    @PostMapping("/req/logout")
    public String logout(HttpServletResponse servletResponse){
        ResponseCookie logoutCookie = ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE , logoutCookie.toString());
        return "redirect:/req/login";
    }
}
