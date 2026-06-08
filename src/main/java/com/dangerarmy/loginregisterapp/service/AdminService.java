package com.dangerarmy.loginregisterapp.service;


import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.model.UserRoles;
import com.dangerarmy.loginregisterapp.repo.UserRepo;
import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void addAdmin(UserModel admin){
        System.out.println("Add admin method call with details: "+admin);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        UserModel dbAdmin = userRepo.save(admin);
        userRolesRepo.save(new UserRoles(null,dbAdmin,"USER"));
        System.out.println("User role was added");
        userRolesRepo.save(new UserRoles(null,dbAdmin,"ADMIN"));
        System.out.println("Admin Role was added");
    }
}
