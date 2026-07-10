package com.dangerarmy.loginregisterapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    
    @GetMapping("/req/login")
    public String login(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())){
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/req/signup")
    public String signup(){
        return "signup";
    }

    @GetMapping("/")
    public String homepage(){
        return "index";
    }

    @GetMapping("/admin/showAdminPanel")
    public String showAdminPanel(){
        return "admin";
    }
}
