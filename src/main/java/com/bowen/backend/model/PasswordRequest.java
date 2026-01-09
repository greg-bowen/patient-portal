package com.bowen.backend.model;

import lombok.Data;

@Data
public class PasswordRequest {
    private String email;
    private String password;
    private String newPassword;
    private String token;
}
