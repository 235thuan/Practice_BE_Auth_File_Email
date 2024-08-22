package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class UserAlreadyExistsException extends RuntimeException {
     public UserAlreadyExistsException(String message) {
        super(message);
    }
}
