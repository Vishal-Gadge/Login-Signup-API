package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.dto.UserRequest;
import com.dangerarmy.loginregisterapp.exception.ExpiredTokenException;
import com.dangerarmy.loginregisterapp.exception.InvalidException;
import com.dangerarmy.loginregisterapp.exception.WeakPasswordException;
import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;
import lombok.RequiredArgsConstructor;
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
public class ForgotPassService {

    private final RedisRateLimiter redisRateLimiter;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public void forgotPassword(@RequestBody UserRequest userRequest) {
        emailService.isValidEmail(userRequest.getEmail());

        redisRateLimiter.rateLimiter("Forgot_pass_attempts_"+userRequest.getEmail(),
                1,Duration.ofSeconds(330));

        Optional<UserModel> dbuser = userRepo.findByEmail(userRequest.getEmail());

        if(dbuser.isEmpty()){
            throw new UsernameNotFoundException("User not exist for that email: "+userRequest.getEmail());
        }

        if(userRequest.getPassword().length() < 3){
            throw new WeakPasswordException("Password is weak, Please consider defining strong password or " +
                    "try combination of letters,numbers and symbols");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        emailService.sendVerificationOtp(userRequest.getEmail(),otp);

        String key = "reset_password:"+otp;
        String hashPass = passwordEncoder.encode(userRequest.getPassword());

        //storing in redis hash
        stringRedisTemplate.opsForHash().put(key, "email", userRequest.getEmail());
        stringRedisTemplate.opsForHash().put(key, "hashedPass", hashPass);
        stringRedisTemplate.expire(key, Duration.ofMinutes(5));
    }


    //after sending email, for verification
    public void verifyForgotPass(String otp) {
        //same otp will not be spammed although its not good cause attacker will try with different comp
        //todo : rate limit on ip address
        redisRateLimiter.rateLimiter("verify_otp_forgotPass_"+otp,3,Duration.ofSeconds(300));

        //null and length check
        if(otp.length() != 6){
            throw new InvalidException("OTP is invalid");
        }

        String key = "reset_password:"+otp;
        if(!otpExists(key)){
            throw new ExpiredTokenException("OTP has been expired, Please try after 5 min");
        }

        //save password , nuke cache
        Optional<UserModel> dbuser = userRepo.findByEmail(getEmail(key));
        dbuser.orElseThrow().setPassword(getHashedPass(key));
        deleteCacheKey(key);
        userRepo.save(dbuser.orElseThrow());
        userRepo.flush();
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
