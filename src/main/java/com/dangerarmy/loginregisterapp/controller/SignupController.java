package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.model.UserRoles;
import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import com.dangerarmy.loginregisterapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;

@RestController
public class SignupController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @Autowired
    private PasswordEncoder encoder12;

    @Autowired
    private EmailService emailService;

    @PostMapping("/req/signup/save")
    public ResponseEntity<String> signup(@RequestBody UserModel user){

        String verificationToken = "fim";

        Optional<UserModel> existingAppUser = userRepo.findByEmail(user.getEmail());
        if(existingAppUser.isPresent()){
            if(existingAppUser.orElseThrow().isVerified()){
                return new ResponseEntity<>("User Already exist and Verified.", HttpStatus.BAD_REQUEST);
            }else{
                //token creation
//                byte[] bytes = new byte[8];
//                new SecureRandom().nextBytes(bytes);
//                String verificationToken = HexFormat.of().formatHex(bytes);
                existingAppUser.orElseThrow().setToken(verificationToken);
                userRepo.save(existingAppUser.orElseThrow());
                //send Email code
                emailService.sendVerificationEmail(existingAppUser.orElseThrow().getEmail(), verificationToken);
                return new ResponseEntity<>("Verification Email resent, Check your inbox",HttpStatus.OK);
            }
        }
        user.setPassword(encoder12.encode(user.getPassword()));
//        byte[] bytes = new byte[8];
//        new SecureRandom().nextBytes(bytes);
//        String verificationToken = HexFormat.of().formatHex(bytes);
        user.setToken(verificationToken);
        UserModel dbuser = userRepo.save(user);
        //send email code
        emailService.sendVerificationEmail(user.getEmail() , verificationToken);
        userRolesRepo.save(new UserRoles(null,dbuser,"USER"));
        return new ResponseEntity<>("Signup successful, check email",HttpStatus.CREATED);
    }
}