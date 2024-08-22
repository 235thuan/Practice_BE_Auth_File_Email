package com.example.authenticationauthorization.configuration.exception;

import com.example.authenticationauthorization.configuration.exception.RunTimeException.*;
import com.example.authenticationauthorization.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.management.relation.RoleNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // tạo global exception string, thông báo lỗi sai định dạng file 400
    @ExceptionHandler(InvalidDataTypeException.class)
    public ResponseEntity<String> handleInvalidDataTypeException(InvalidDataTypeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //tạo 1 global exception string, thông báo lỗi server 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Internal server error"+ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // lỗi 400
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MissingAuthenticationDetailsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMissingAuthenticationDetailsException(MissingAuthenticationDetailsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidAuthenticationDetailsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleInvalidAuthenticationDetailsException(InvalidAuthenticationDetailsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(MissingRequiredFieldsException.class)
    public ResponseEntity<String> handleMissingRequiredFieldsException(MissingRequiredFieldsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + ex.getMessage() + "\"}");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleCustomAuthenticationException(AuthException e) {
        return new ResponseEntity<>(e.getMessage()+" Authentication alert", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<String> handlePermissionNotFoundException(PermissionNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
