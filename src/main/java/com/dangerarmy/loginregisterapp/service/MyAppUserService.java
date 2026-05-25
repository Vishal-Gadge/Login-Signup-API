package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.model.MyUserDetailsModel;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
@Primary
public class MyAppUserService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        UserModel user = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found in db for that email"));

        return new MyUserDetailsModel(user);
    }

    @Transactional
    public void updatePassword(String email , String username, String newPassword){

        String encodedPass = new BCryptPasswordEncoder(12).encode(newPassword);

        int rowsUpdated = userRepo.updatePasswordByEmailAndUsername(email,username,encodedPass);

        if(rowsUpdated == 0){
            throw new UsernameNotFoundException("User not found or username dosen't match email");
        }
    }
}
