package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.model.MyUserDetailsModel;
import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.repo.UserRepo;

import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
@Primary
public class MyAppUserService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        UserModel user = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found in db for that email"));

        List<String> roles = userRolesRepo.getRoles(user.getId());
        return new MyUserDetailsModel(user,roles);
    }
}
