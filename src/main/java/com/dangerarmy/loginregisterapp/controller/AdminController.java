package com.dangerarmy.loginregisterapp.controller;

import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.model.UserRoles;
import com.dangerarmy.loginregisterapp.repo.UserRepo;
import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRolesRepo userRolesRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/getAllUsers")
    public List<UserModel> getAllUsers(){
        return userRepo.findAll();
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<Map<String, String>> addAdmin(@RequestBody UserModel admin){
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        userRepo.save(admin);
        userRolesRepo.save(new UserRoles(null,admin,"ADMIN"));
        userRolesRepo.save(new UserRoles(null,admin,"USER"));
        return ResponseEntity.ok(Map.of("message","Admin Saved Successfully"));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAdmin(){
        try{
            return ResponseEntity.ok(Map.of("message","You are now Admin"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error"," You are not Admin"));
        }
      }
}
