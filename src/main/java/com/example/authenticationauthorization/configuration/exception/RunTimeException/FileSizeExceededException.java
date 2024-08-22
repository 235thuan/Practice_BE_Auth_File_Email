package com.example.authenticationauthorization.configuration.exception.RunTimeException;


public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException() {
        super("Total file size exceeds the 10 MB limit.");
    }

    public FileSizeExceededException(String message) {
        super(message);
    }
}

