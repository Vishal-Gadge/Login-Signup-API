package com.dangerarmy.loginregisterapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

@Service
public class LoginService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder12;

    public UserModel verifyAndGetUser(String email , String password) {
        UserModel user = userRepo.findByEmail(email).orElse(null);
        if (user != null && encoder12.matches(password , user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean verifyEmail(String token){
        UserModel dbuser = userRepo.findByToken(token);
        if(dbuser != null){
            dbuser.setVerified(true);
            dbuser.setToken(null);
            userRepo.save(dbuser);
        }
        return false;
    }
}
