package com.dangerarmy.loginregisterapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "token")
    private String token;

    @Column(name = "expiresAt")
    private Date expiresAt;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    public VerifyUser(UserModel userModel, String token ){
        this.userModel = userModel;
        this.token = token;
        this.expiresAt = new Date(System.currentTimeMillis() + (1000*60*10));
    }

    public VerifyUser(){
        this.expiresAt = new Date(System.currentTimeMillis() + (1000*60*10));
    }
}
