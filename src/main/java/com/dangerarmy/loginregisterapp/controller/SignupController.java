package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.model.UserRoles;
import com.dangerarmy.loginregisterapp.model.VerifyUser;
import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import com.dangerarmy.loginregisterapp.repo.VerifyUserRepo;
import com.dangerarmy.loginregisterapp.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
import java.util.Date;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SignupController {

    private final UserRepo userRepo;
    private final UserRolesRepo userRolesRepo;
    private final VerifyUserRepo verifyUserRepo;
    private final PasswordEncoder encoder12;
    private final EmailService emailService;

    @PostMapping("/req/signup/save")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserModel user){

        //valid email checkup
        emailService.isValidEmail(user.getEmail());

        Optional<UserModel> existingAppUser = userRepo.findByEmail(user.getEmail());
        if(existingAppUser.isPresent()){
            VerifyUser verifyUser = verifyUserRepo.findByUserModel(existingAppUser.orElseThrow());
            if(verifyUser == null){
                verifyUser = new VerifyUser(existingAppUser.orElseThrow(), emailService.generateToken());
            }
            if(verifyUser.isVerified()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error","User Already exist and Verified."));
            }else{
                String verificationToken = emailService.generateToken();

                verifyUser.setToken(verificationToken);
                verifyUser.setExpiresAt(new Date(System.currentTimeMillis()+1000*60*10));
                verifyUserRepo.save(verifyUser);

                userRepo.save(existingAppUser.orElseThrow());

                emailService.sendVerificationEmail(existingAppUser.orElseThrow().getEmail(), verificationToken);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message","Verification Email resent, Check your inbox"));
            }
        }
        user.setPassword(encoder12.encode(user.getPassword()));

        String verificationToken = emailService.generateToken();

        UserModel dbuser = userRepo.save(user);
        userRolesRepo.save(new UserRoles(null,dbuser,"USER"));

        emailService.sendVerificationEmail(user.getEmail() , verificationToken);

        VerifyUser verifyUser = new VerifyUser(dbuser,verificationToken);
        verifyUserRepo.save(verifyUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message","Signup successful, check email"));
    }
}