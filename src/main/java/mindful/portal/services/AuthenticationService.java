package mindful.portal.services;


import mindful.portal.model.PasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JdbcClient jdbcClient;
    final int PASSWORD_RESET_EXPIRATION = 15;

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
                                response.put("valid", false);
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

    public JSONObject resetPassword(PasswordRequest request) {
        AtomicReference<String> message = new AtomicReference<>();
        try {
            jdbcClient.sql("SELECT pat.patient_id, pass.hashed_password, e.id email_id " +
                            "FROM core_bio.passwords pass, core_bio.patients pat, core_bio.emails e " +
                            "WHERE pass.patient_id = pat.patient_id " +
                            "and pat.patient_id = e.patient_id " +
                            "and e.expiration_date is null " +
                            "and e.email = ?"
                    )
                    .param(request.getEmail().toLowerCase())
                    .query((rs, rowNum) -> Map.of(
                            "patient_id", rs.getObject("patient_id"),
                            "hashed_password", rs.getString("hashed_password"),
                            "email_id", rs.getObject("email_id")
                    ))
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(row -> {// user found in db
                                // save token
                                //   BONUS - if threshold is not reached
                                log.info("User found for email reset: {}", request.getEmail());

                                int resetCount = jdbcClient.sql("SELECT COUNT(*) " +
                                                "FROM core_bio.password_resets " +
                                                "WHERE patient_id = ? " +
                                                "AND requested_at <= NOW() " +
                                                "AND expires_at >= NOW()")
                                        .param(row.get("patient_id"))
                                        .query(Integer.class)
                                        .single();

                                log.info("Password resets active for patient {}: {} ({} remaining)", row.get("patient_id"), resetCount, 3 - resetCount);

                                if (resetCount >= 3) {
                                    log.warn("Password reset limit exceeded for patient: {}", row.get("patient_id"));
                                    message.set("Password reset limit has been exceeded for " + PASSWORD_RESET_EXPIRATION + " minutes. " +
                                            "Please double check your Inbox and Spam folders.");
                                    return;
                                }

                                // generate a token
                                String token = UUID.randomUUID().toString();
                                // save the token (hashed for security)
                                String hashedToken = hashPassword(token);
                                jdbcClient.sql("insert into core_bio.password_resets(patient_id, email_id, hashed_token, expires_at) " +
                                                "values (:patientId, :emailId, :hashedToken, :expiresAt)")
                                        .param("patientId", row.get("patient_id"))
                                        .param("emailId", row.get("email_id"))
                                        .param("hashedToken", hashedToken)
                                        .param("expiresAt", LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRATION))
                                        .update();

                                // TODO - send email with reset link with token as parameter

                                message.set("If an account exists for this email, a password reset link has been sent.");
                            },
                            () -> {// user not found in db
                                log.info("User not found for email reset: {}", request.getEmail());
                            });
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            return new JSONObject()
                    .put("valid", false)
                    .put("message", "An error occurred while resetting password");
        }
        return new JSONObject()
                .put("valid", true)
                .put("message", message);
    }

    public JSONObject confirmPassword(PasswordRequest request) {
        String email = request.getEmail();
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        boolean exists = jdbcClient.sql("SELECT COUNT(*) " +
                        "FROM core_bio.password_resets pr, core_bio.emails e " +
                        "WHERE pr.patient_id = e.patient_id " +
                        "and e.email = ? " +
                        "AND hashed_token = ? " +
                        "AND expires_at >= NOW()")
                .param(email)
                .param(hashPassword(token))
                .query(Integer.class)
                .single() > 0;
        if(!exists) {
            return new JSONObject()
                    .put("valid", false)
                    .put("message", "This password reset is no longer valid");
        }

        // expire the existing password reset tokens
        jdbcClient.sql("update core_bio.password_resets pr " +
                        "set expires_at = now() " +
                        "where pr.patient_id = " +
                        "(select e.patient_id from core_bio.emails e where e.email = ?)")
                .param(email)
                .update();

        // insert new password (table triggers retires the old and adds updated seq_id)
        jdbcClient.sql("insert into core_bio.passwords(patient_id, hashed_password) " +
                        "values ((select e.patient_id from core_bio.emails e where e.email = ?), ?)")
                .param(email)
                .param(hashPassword(newPassword))
                .update();
        return new JSONObject()
                .put("valid", true)
                .put("message", "Password reset successfully!");
    }
}
