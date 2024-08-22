package com.example.authenticationauthorization.configuration.exception.RunTimeException;

public class InvalidDataTypeException extends RuntimeException {

    // tạo custom thông báo lỗi sai kiểu dữ liệu
    public InvalidDataTypeException(String message) {
        super(message);
    }
}
