package com.bowen.backend.services;


import com.bowen.backend.model.PasswordRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JdbcClient jdbcClient;

    public JSONObject validatePassword(PasswordRequest request) {
        if(!hasText(request.getPassword()))
            return new JSONObject().put("valid", false).put("message", "Current password is required");

        JSONObject response = new JSONObject();
        jdbcClient.sql("SELECT hashed_password " +
                        "FROM public.users " +
                        "WHERE email = ?")
                .param(request.getEmail().toLowerCase())
                .query(String.class)
                .stream()
                .findFirst()
                .ifPresentOrElse(password -> {// user found in db
                            if (verifyPassword(request.getPassword(), password)) {
                                response.put("valid", true);
                                response.put("message", "Password is correct");
                            } else {
                                response.put("valid", true);
                                response.put("message", "Password is incorrect");
                            }
                        },
                        () -> {// user not found in db
                            response.put("valid", false);
                            response.put("message", "User not found");});
        return response;
    }

    public JSONObject updatePassword(PasswordRequest request) {
        if(!hasText(request.getPassword()))
            return new JSONObject().put("valid", false).put("message", "Current password is required");
        else if(!hasText(request.getNewPassword()))
            return new JSONObject().put("valid", false).put("message", "New password is required");
        else if(!hasText(request.getEmail()))
            return new JSONObject().put("valid", false).put("message", "Email is required");
        else if(request.getNewPassword().equals(request.getPassword()))
            return new JSONObject().put("valid", false).put("message", "Passwords do not match");


        JSONObject validatedPassword = validatePassword(request);
        if(validatedPassword.getBoolean("valid")) {
            jdbcClient.sql("UPDATE public.users " +
                            "SET hashed_password = ? " +
                            "WHERE email = ?")
                    .param(hashPassword(request.getNewPassword()))
                    .param(request.getEmail().toLowerCase())
                    .update();
            return new JSONObject()
                    .put("valid", true)
                    .put("message", "Password updated successfully");
        } else {
            return validatedPassword;
        }
    }

    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return new BCryptPasswordEncoder().matches(plainPassword, hashedPassword);
    }

    private String hashPassword(String plainPassword) {
        return new BCryptPasswordEncoder().encode(plainPassword);
    }
}
