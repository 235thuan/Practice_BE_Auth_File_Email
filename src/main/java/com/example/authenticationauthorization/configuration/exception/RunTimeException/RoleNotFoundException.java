package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
