package com.fighttracker.fighttracker_backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    @NotBlank(message = "Email è obbligatoria")
    private String email;

    @NotBlank(message = "Password è obbligatoria")
    private String password;

    public AuthRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
