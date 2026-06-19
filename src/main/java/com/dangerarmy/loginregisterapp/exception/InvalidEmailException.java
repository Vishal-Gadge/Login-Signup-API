package com.dangerarmy.loginregisterapp.exception;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message){
        super(message);
    }
}
