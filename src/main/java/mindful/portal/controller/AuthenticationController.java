package mindful.portal.controller;

import mindful.portal.model.PasswordRequest;
import mindful.portal.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping(value = "/validate-password", consumes = "application/json")
    public ResponseEntity<String> validatePassword(@RequestBody PasswordRequest request) {
        log.info("request: {}", request);
        JSONObject response = authService.validatePassword(request);
        log.info("response: {}", response);
        return ResponseEntity.ok(response.toString());
    }

    @PutMapping(value = "/update-password", consumes = "application/json")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordRequest request) {
        JSONObject response = authService.updatePassword(request);

        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(value = "/reset-password/request", consumes = "application/json")
    public ResponseEntity<String> requestPasswordRequest(@RequestBody PasswordRequest request) {
        JSONObject response = authService.resetPassword(request);

        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(value = "/reset-password/confirm", consumes = "application/json")
    public ResponseEntity<String> confirmPasswordRequest(@RequestBody PasswordRequest request) {
        JSONObject response = authService.confirmPassword(request);

        return ResponseEntity.ok(response.toString());
    }
}