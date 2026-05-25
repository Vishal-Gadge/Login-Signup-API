package com.dangerarmy.loginregisterapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {

    @PostMapping("/req/logout")
    public String logout(HttpServletResponse servletResponse){
        System.out.println("\n\n Logout has triggered");
        ResponseCookie logoutCookie = ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE , logoutCookie.toString());
        System.out.println("logout successful");
        return "redirect:/req/login";
    }
}
