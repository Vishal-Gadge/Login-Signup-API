package com.dangerarmy.loginregisterapp.exception;

public class UserAlreadyVerifiedException extends RuntimeException{
    public UserAlreadyVerifiedException(String message){
        super(message);
    }
}
