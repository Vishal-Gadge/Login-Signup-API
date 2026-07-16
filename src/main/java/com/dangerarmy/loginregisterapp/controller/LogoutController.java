package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/req/logout")
    public String logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse){
        logoutService.logout(servletRequest, servletResponse);
        return "redirect:/req/login";
    }
}
