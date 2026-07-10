package com.dangerarmy.loginregisterapp.service;

import java.nio.charset.StandardCharsets;
import java.util.*;

import com.dangerarmy.loginregisterapp.repo.UserRolesRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.dangerarmy.loginregisterapp.model.UserModel;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;

@Service
@Slf4j
public class JwtService {

    @Autowired
    private UserRolesRepo userRolesRepo;

    private final SecretKey key;

    @Value("${jwt.expiration}")
    private long expiration;

    public JwtService(@Value("${jwt.secret}")String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(UserModel user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("email",user.getEmail());
        claims.put("roles",userRolesRepo.getRoles(user.getId()));
        return Jwts.builder()
                    .claims(claims)
                    .subject(user.getEmail())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis()+expiration))
                    .signWith(key)
                    .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    public boolean verifyToken(String username , Claims claims) {
        try{
            String subject = claims.getSubject();
            Date expiry = claims.getExpiration();
            return subject.equals(username) && expiry.after(new Date());
        }catch (Exception e){
            log.error("Token verification failed",e);
            return false;
        }
    }
}
