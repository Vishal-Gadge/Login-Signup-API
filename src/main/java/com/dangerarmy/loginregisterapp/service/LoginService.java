package com.dangerarmy.loginregisterapp.service;

import java.time.Duration;
import java.util.Optional;

import com.dangerarmy.loginregisterapp.exception.InvalidException;
import com.dangerarmy.loginregisterapp.model.VerifyUser;
import com.dangerarmy.loginregisterapp.repo.VerifyUserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dangerarmy.loginregisterapp.dto.UserRequest;
import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepo userRepo;
    private final VerifyUserRepo verifyUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RedisRateLimiter redisRateLimiter;

    public void login(UserRequest req, HttpServletRequest request, HttpServletResponse response) {
        //1hr 10 chances for ip so attacker won't spam email
        redisRateLimiter.rateLimiter("login_limit_ip_"+request.getRemoteAddr() , 10, Duration.ofSeconds(3600));

        //10min 3 chances for 1 email so attacker won't spam passwords
        redisRateLimiter.rateLimiter("login_limit_email_"+req.getEmail(), 3, Duration.ofSeconds(600));

        if(req.getEmail().isEmpty() || req.getPassword().isEmpty()){
            throw new InvalidException("Credentials cannot be empty");
        }

        //is valid email , will throw exception also if invalid
        emailService.isValidEmail(req.getEmail());

        Optional<UserModel> dbuser = userRepo.findByEmail(req.getEmail());

        //didn't get user for the email
        if(dbuser.isEmpty()){
            throw new InvalidException("Invalid Credentials");
        }

        //password check
        if(!passwordEncoder.matches(req.getPassword() , dbuser.orElseThrow().getPassword())){
            log.warn("Password don't match for email :{}",emailService.maskEmail(req.getEmail()));
            throw new InvalidException("Invalid Credentials");
        }

        VerifyUser dbVerifyUser = verifyUserRepo.findByUserModel(dbuser.orElseThrow());

        //if not verified
        if(!dbVerifyUser.isVerified()){
            throw new InvalidException("User not verified");
        }

        //generate jwt
        String jwt = jwtService.generateToken(dbuser.orElseThrow());
        log.info("Token is :{}",jwt);

        //response cookie for 1 day
        ResponseCookie responseCookie = ResponseCookie.from("jwt",jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .sameSite("LAX")
                .build();

        //adding response cookie to header
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        log.info("User Successfully login with email :{}",emailService.maskEmail(req.getEmail()));
    }
}
