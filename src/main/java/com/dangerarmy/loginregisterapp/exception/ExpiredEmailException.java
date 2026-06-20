package com.dangerarmy.loginregisterapp.exception;

public class ExpiredEmailException extends RuntimeException{
    public ExpiredEmailException (String message){
        super(message);
    }
}
