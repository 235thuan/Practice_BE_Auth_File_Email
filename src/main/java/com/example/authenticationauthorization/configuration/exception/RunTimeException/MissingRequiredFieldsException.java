package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class MissingRequiredFieldsException extends RuntimeException {
    public MissingRequiredFieldsException(String message) {
        super(message);
    }
}
