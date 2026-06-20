package com.dangerarmy.loginregisterapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String,String>> handleInvalidToken(InvalidTokenException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<Map<String, String>> handleExpiredToken(ExpiredTokenException e){
        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyVerified(UserAlreadyVerifiedException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UsernameNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<Map<String, String>> handleUnknownHost(UnknownHostException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message",e.getMessage()));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEmail(InvalidEmailException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message","Email is not valid"));
    }

    @ExceptionHandler(ExpiredEmailException.class)
    public ResponseEntity<?> handleExpiredEmail(ExpiredEmailException e){
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message",e.getMessage()));
    }
}

















