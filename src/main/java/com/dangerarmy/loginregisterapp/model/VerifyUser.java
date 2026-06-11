package com.dangerarmy.loginregisterapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class VerifyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    private String token;
    private Date expiresAt;
    private boolean isVerified = false;

    public VerifyUser(UserModel userModel, String token ){
        this.userModel = userModel;
        this.token = token;
        this.expiresAt = new Date(System.currentTimeMillis() + (1000*60*10));
    }
}
