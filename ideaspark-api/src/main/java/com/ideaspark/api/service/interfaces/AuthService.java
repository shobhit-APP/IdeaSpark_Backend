package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.*;

public interface AuthService {
    
    // Existing methods
    ResponseDTO<Object> login(LoginRequest loginRequest);
    
    ResponseDTO<Object> refreshToken(String refreshToken);
    
    ResponseDTO<String> logout(String token);
    
    ResponseDTO<String> resetPassword(String email);
    
    ResponseDTO<String> changePassword(String email, String currentPassword, String newPassword);
    
    // Enhanced authentication methods
    ResponseDTO<Object> multiLogin(com.ideaspark.shared.dto.MultiLoginRequest request);
    ResponseDTO<Object> googleLogin(GoogleLoginRequest request);
    
    ResponseDTO<String> forgotPassword(ForgotPasswordRequest request);
    
    ResponseDTO<String> resetPasswordWithOtp(ResetPasswordRequest request);
    
    ResponseDTO<String> sendOtp(String phoneOrEmail, String purpose);
    
    ResponseDTO<String> verifyOtp(String phoneOrEmail, String otp, String purpose);
    
    ResponseDTO<String> verifyPhoneNumber(String userId, String otp);
    
    ResponseDTO<String> verifyEmail(String userId, String otp);

    // Login using phone/email + OTP (uses OtpService persistence)
    ResponseDTO<Object> loginWithOtp(String phoneOrEmail, String otp);
    
    boolean validateToken(String token);
    
    String extractEmailFromToken(String token);
    
    // User blocking check
    boolean isUserBlocked(String userId);
    
    // Registration with image upload (now accepts username and phone)
    ResponseDTO<UserDTO> register(String email, String password, String fullName, String username, String phone, String role, org.springframework.web.multipart.MultipartFile profileImage);
}