package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//to show html forms only
@Controller
public class ContentController {
    
    @GetMapping("/req/login")
    public String login(){
        System.out.println("Login controller hit. Auth: "+
                SecurityContextHolder.getContext().getAuthentication());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())){
            System.out.println("User already authenticated");
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
        System.out.println("Home page git - user is logged in");
        return "index";
    }

    @GetMapping("/admin/showAdminPanel")
    public String showAdminPanel(){
        return "admin";
    }
}
