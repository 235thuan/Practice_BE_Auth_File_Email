package com.example.authenticationauthorization.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
