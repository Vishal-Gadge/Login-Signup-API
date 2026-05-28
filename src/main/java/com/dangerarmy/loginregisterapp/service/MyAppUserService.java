package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.model.MyUserDetailsModel;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

import lombok.AllArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
@Primary
public class MyAppUserService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        UserModel user = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found in db for that email"));

        return new MyUserDetailsModel(user);
    }

    @Transactional
    public void updatePassword(UserModel userModel){
        String key = "rate_limit_"+userModel.getEmail();
        String attemptStr = redisTemplate.opsForValue().get(key);
        long redisAttempt = attemptStr == null ? 0 : Long.parseLong(attemptStr);

        if (redisAttempt >= 3){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failure requests. Try again after 10 min");
        }

        String encodedPass = new BCryptPasswordEncoder(12).encode(userModel.getPassword());
        int rowsUpdated = userRepo.updatePasswordByEmailAndUsername(userModel.getEmail(),
                userModel.getUsername(),encodedPass);

        if(rowsUpdated == 0){
            long attempts = redisTemplate.opsForValue().increment(key);
            if(attempts == 1){
                redisTemplate.expire(key , Duration.ofMinutes(10));
            }
            if(attempts > 3){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failure requests. Try again after 10 min");
            }
            throw new UsernameNotFoundException("User not found or username dosen't match email");
        }
    }
}
