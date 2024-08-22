package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
