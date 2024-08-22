package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class MissingAuthenticationDetailsException extends RuntimeException{
    public MissingAuthenticationDetailsException(String message) {
        super(message);
    }
}
