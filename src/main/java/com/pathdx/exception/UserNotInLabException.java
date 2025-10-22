package com.pathdx.exception;

public class UserNotInLabException extends RuntimeException{
    public UserNotInLabException(String message) {
        super(message);
    }
}
