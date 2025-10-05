package com.ideaspark.api.controller;

import com.ideaspark.api.service.interfaces.AuthService;
import com.ideaspark.shared.dto.LoginRequest;
import com.ideaspark.shared.dto.MultiLoginRequest;
import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
// ...existing imports...
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {


    private final AuthService authService;

        /**
         * Register a new user
         * @param email User email
         * @param password User password
         * @param fullName User full name
         * @param role User role (default USER)
         * @param profileImage Profile image file (optional)
         * @return Registered user details
         */
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Register a new user",
            description = "Creates a user with optional profile image"
        )
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
        })
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> register(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "role", defaultValue = "USER") String role,
            @RequestParam(value = "profileImage", required = false) org.springframework.web.multipart.MultipartFile profileImage
    ) {
        ResponseDTO<UserDTO> response = authService.register(email, password, fullName, username, phone, role, profileImage);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        ResponseDTO<Object> response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // Multi-login removed: login is handled via email or phone only
    @PostMapping("/multi-login")
    public ResponseEntity<ResponseDTO<Object>> multiLogin(@Valid @RequestBody com.ideaspark.shared.dto.MultiLoginRequest request) {
        ResponseDTO<Object> response = authService.multiLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO<Object>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            ResponseDTO<Object> response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Invalid token format"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            ResponseDTO<String> response = authService.logout(token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Invalid token format"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        ResponseDTO<String> response = authService.resetPassword(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@RequestBody com.ideaspark.shared.dto.ForgotPasswordRequest request) {
        ResponseDTO<String> response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ResponseDTO<String>> sendOtp(@RequestBody Map<String, String> request) {
        String phoneOrEmail = request.get("phoneOrEmail");
        String purpose = request.get("purpose");
        ResponseDTO<String> response = authService.sendOtp(phoneOrEmail, purpose);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseDTO<String>> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneOrEmail = request.get("phoneOrEmail");
        String otp = request.get("otp");
        String purpose = request.get("purpose");
        ResponseDTO<String> response = authService.verifyOtp(phoneOrEmail, otp, purpose);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-with-otp")
    public ResponseEntity<ResponseDTO<Object>> loginWithOtp(@RequestBody Map<String, String> request) {
        String phoneOrEmail = request.get("phoneOrEmail");
        String otp = request.get("otp");
        ResponseDTO<Object> response = authService.loginWithOtp(phoneOrEmail, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password-otp")
    public ResponseEntity<ResponseDTO<String>> resetPasswordWithOtp(@RequestBody com.ideaspark.shared.dto.ResetPasswordRequest request) {
        ResponseDTO<String> response = authService.resetPasswordWithOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ResponseDTO<String>> verifyEmail(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String otp = request.get("otp");
        ResponseDTO<String> response = authService.verifyEmail(userId, otp);
        return ResponseEntity.ok(response);
    }
}