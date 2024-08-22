package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class InvalidAuthenticationDetailsException extends RuntimeException {
    public InvalidAuthenticationDetailsException(String message) {
        super(message);
    }
}
