package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.dto.UserRequest;
import com.dangerarmy.loginregisterapp.exception.ExpiredTokenException;
import com.dangerarmy.loginregisterapp.exception.InvalidException;
import com.dangerarmy.loginregisterapp.exception.WeakPasswordException;
import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPassService {

    private final RedisRateLimiter redisRateLimiter;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public void forgotPassword(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        //for not spamming emails from one host ip for forgotPassword
        redisRateLimiter.rateLimiter("forgot_pass_atm_ip_"+request.getRemoteAddr(),
                10,Duration.ofSeconds(900));

        emailService.isValidEmail(userRequest.getEmail());

        if(userRequest.getPassword().length() < 3){
            throw new WeakPasswordException("Password is weak, Please consider defining strong password or " +
                    "try combination of letters,numbers and symbols");
        }

        Optional<UserModel> dbuser = userRepo.findByEmail(userRequest.getEmail());

        if(dbuser.isEmpty()){
            //sleep added so that it matches the time of email process step
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.error("User not found for forgot password service with email :{}",emailService.maskEmail(userRequest.getEmail()));
            throw new UsernameNotFoundException("If User present for the email, OTP has been sent");
        }

        //get only one chance so that only 1 otp exist for valid user email
        redisRateLimiter.rateLimiter("forgot_pass_atm_email_"+userRequest.getEmail(),
                1,Duration.ofSeconds(330));

        //generate otp through secure random inbuild class
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        emailService.sendVerificationOtp(userRequest.getEmail(),otp);

        String key = "reset_password:"+otp;
        String hashPass = passwordEncoder.encode(userRequest.getPassword());

        //storing in redis hash
        stringRedisTemplate.opsForHash().put(key, "email", userRequest.getEmail());
        stringRedisTemplate.opsForHash().put(key, "hashedPass", hashPass);
        stringRedisTemplate.expire(key, Duration.ofMinutes(5));
        log.info("Forgot password request was successful for email :{}",emailService.maskEmail(userRequest.getEmail()));
    }


    //after sending email, for verification
    public void verifyForgotPass(String otp, HttpServletRequest request) {
        //for not spamming emails from one host ip for forgotPassword
        redisRateLimiter.rateLimiter("forgot_pass_atm_ip_"+request.getRemoteAddr(),
                10,Duration.ofSeconds(900));

        //same otp will not be spammed although its not good cause attacker will try with different comp
        redisRateLimiter.rateLimiter("verify_otp_forgotPass_"+otp,3,Duration.ofSeconds(300));

        //null and length check
        if(otp.length() != 6){
            throw new InvalidException("OTP is invalid");
        }

        String key = "reset_password:"+otp;
        if(!otpExists(key)){
            log.warn("OTP has expired of forgot password for the ip :{}",request.getRemoteAddr().substring(0,10));
            throw new ExpiredTokenException("OTP has been expired, Please try later");
        }

        //save password , nuke cache
        Optional<UserModel> dbuser = userRepo.findByEmail(getEmail(key));
        dbuser.orElseThrow().setPassword(getHashedPass(key));
        deleteCacheKey(key);
        userRepo.save(dbuser.orElseThrow());
        log.info("Verified OTP and Password has been changed for email :{}",
                emailService.maskEmail(dbuser.orElseThrow().getEmail()));
    }

    //utility
    public String getEmail(String key){
        return (String) stringRedisTemplate.opsForHash().get(key,"email");
    }

    public String getHashedPass(String key){
        return (String) stringRedisTemplate.opsForHash().get(key,"hashedPass");
    }

    public boolean otpExists(String key){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public void deleteCacheKey(String key){
        stringRedisTemplate.delete(key);
    }
}
