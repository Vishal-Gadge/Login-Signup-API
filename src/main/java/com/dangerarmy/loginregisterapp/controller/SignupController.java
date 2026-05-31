package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.model.UserRoles;
import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
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
    private UserRolesRepo userRolesRepo;

    @Autowired
    private PasswordEncoder encoder12;

    @PostMapping("/req/signup/save")
    public void signup(@RequestBody UserModel user){
        user.setPassword(encoder12.encode(user.getPassword()));
        UserModel dbuser = userRepo.save(user);
        userRolesRepo.save(new UserRoles(null,dbuser,"USER"));
    }
}