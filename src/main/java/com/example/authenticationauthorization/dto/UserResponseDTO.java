package com.example.authenticationauthorization.dto;


import com.example.authenticationauthorization.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long userId;
    private String userName;
    private String email;
    private String status;
    private Set<Role> roles;
    private String message;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
