package com.dangerarmy.loginregisterapp.exception;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(String message){
        super(message);
    }
}
