package com.example.authenticationauthorization.configuration.exception.RunTimeException;

import java.io.IOException;

public class UnsupportedFileTypeException extends IOException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
