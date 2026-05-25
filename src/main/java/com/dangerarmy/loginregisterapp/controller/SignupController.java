package com.dangerarmy.loginregisterapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

@RestController
public class SignupController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder12;

    @PostMapping("/req/signup/save")
    public String signup(@RequestBody UserModel user){
        user.setPassword(encoder12.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/req/login";
    }
}