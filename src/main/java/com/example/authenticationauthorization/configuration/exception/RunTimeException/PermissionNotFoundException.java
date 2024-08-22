package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(String message) {
        super(message);
    }
}
