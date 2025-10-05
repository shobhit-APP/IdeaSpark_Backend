package com.ideaspark.api.service.impl;

import com.ideaspark.api.exception.ExceptionUtils;
import com.ideaspark.api.repository.UserRepository;
import com.ideaspark.api.service.interfaces.AuthService;
import com.ideaspark.api.service.interfaces.UserService;
import com.ideaspark.api.service.JwtService;
import com.ideaspark.shared.dto.*;
import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;
import com.ideaspark.shared.exception.AuthenticationException;
import com.ideaspark.shared.exception.UserBlockedException;
import com.ideaspark.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final com.ideaspark.api.service.interfaces.CloudinaryService cloudinaryService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final com.ideaspark.api.service.interfaces.EmailService emailService;
    private final com.ideaspark.api.service.interfaces.OtpService otpService;
    private final com.ideaspark.api.service.interfaces.SmsService smsService;

    @Override
    public ResponseDTO<UserDTO> register(String email, String password, String fullName, String username, String phone, String role, org.springframework.web.multipart.MultipartFile profileImage) {
        try {
            if (userRepository.existsByEmail(email)) {
                return ResponseDTO.error("EMAIL_EXISTS", "Email already registered");
            }

            if (username != null && !username.isBlank() && userRepository.existsByUsername(username)) {
                return ResponseDTO.error("USERNAME_EXISTS", "Username already taken");
            }

            if (phone != null && !phone.isBlank() && userRepository.existsByPhone(phone)) {
                return ResponseDTO.error("PHONE_EXISTS", "Phone number already registered");
            }

            String imageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    var uploadResult = cloudinaryService.uploadImage(profileImage, "users");
                    imageUrl = (String) uploadResult.get("secure_url");
                } catch (Exception e) {
                    log.error("Image upload failed: {}", e.getMessage());
                }
            }

            User user = User.builder()
                    .email(email)
            .username(username)
            .phone(phone)
                    .passwordHash(passwordEncoder.encode(password))
                    .fullName(fullName)
                    .role(UserRole.valueOf(role.toUpperCase()))
                    .status(UserStatus.ACTIVE)
                    .isActive(true)
                    .isVerified(false)
                    .isPremium(false)
                    .createdAt(LocalDateTime.now())
                    .profileImageUrl(imageUrl)
                    .build();

            User savedUser = userRepository.save(user);
            
            // Log registration activity
            userService.logUserActivity(savedUser, "REGISTER", "auth", "User registered", null, null);

            // Generate verification OTP and send email
            try {
                String otp = String.format("%06d", (int)(Math.random() * 1000000));
                savedUser.setResetPasswordToken(otp); // reusing resetPasswordToken field as verification OTP
                savedUser.setResetPasswordExpiry(LocalDateTime.now().plusMinutes(5));
                userRepository.save(savedUser);
                emailService.sendEmailVerificationOTP(savedUser.getEmail(), otp, savedUser.getFullName());
            } catch (Exception e) {
                log.warn("Failed to send verification email: {}", e.getMessage());
            }

            UserDTO userDTO = convertToUserDTO(savedUser);
            return ResponseDTO.success("User registered successfully. Verification OTP sent to email.", userDTO);

        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            return ResponseDTO.error("REGISTRATION_FAILED", "Failed to register user");
        }
    }

    @Override
    public ResponseDTO<Object> login(LoginRequest loginRequest) {
        try {
        String identifier = loginRequest.getIdentifier();
        String password = loginRequest.getPassword();

        // Determine if identifier is email or phone
        User user;
        if (identifier != null && identifier.contains("@")) {
        user = userRepository.findByEmail(identifier).orElseThrow(() -> new RuntimeException("User not found"));
        } else {
        user = userRepository.findByPhone(identifier).orElseThrow(() -> new RuntimeException("User not found"));
        }

        // Authenticate using email as principal (AuthenticationManager expects username)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                password
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Generate tokens
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("role", user.getRole());
            extraClaims.put("subscriptionType", user.getIsPremium() ? "PREMIUM" : "FREE");

            String token = jwtService.generateTokenWithClaims(userDetails, extraClaims);
            String refreshToken = jwtService.generateToken(userDetails);

            // Log login activity
            userService.logUserActivity(user, "LOGIN", "auth", "User logged in", null, null);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("user", convertToUserDTO(user));

            return ResponseDTO.success("Login successful", response);

        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ResponseDTO.error("LOGIN_FAILED", "Invalid email or password");
        }
    }

    @Override
    public ResponseDTO<Object> refreshToken(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (jwtService.validateToken(refreshToken, 
                    org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPasswordHash())
                            .authorities("ROLE_" + user.getRole().name())
                            .build())) {

                Map<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("userId", user.getId());
                extraClaims.put("role", user.getRole());
                extraClaims.put("subscriptionType", user.getIsPremium() ? "PREMIUM" : "FREE");

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities("ROLE_" + user.getRole().name())
                        .build();

                String newToken = jwtService.generateTokenWithClaims(userDetails, extraClaims);
                String newRefreshToken = jwtService.generateToken(userDetails);

                Map<String, Object> response = new HashMap<>();
                response.put("token", newToken);
                response.put("refreshToken", newRefreshToken);

                return ResponseDTO.success(response);
            } else {
                return ResponseDTO.error("INVALID_TOKEN", "Invalid refresh token");
            }

        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseDTO.error("TOKEN_REFRESH_FAILED", "Failed to refresh token");
        }
    }

    @Override
    public ResponseDTO<String> logout(String token) {
        try {
            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                userService.logUserActivity(user, "LOGOUT", "auth", "User logged out", null, null);
            }
            
            // In a real implementation, you'd add the token to a blacklist
            return ResponseDTO.success("Logout successful");

        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseDTO.error("LOGOUT_FAILED", "Failed to logout");
        }
    }

    @Override
    public ResponseDTO<String> resetPassword(String email) {
        try {
            // Delegate to forgotPassword flow which sends an OTP for password reset
            com.ideaspark.shared.dto.ForgotPasswordRequest request = new com.ideaspark.shared.dto.ForgotPasswordRequest();
            request.setPhoneOrEmail(email);
            return forgotPassword(request);

        } catch (Exception e) {
            log.error("Error sending password reset: {}", e.getMessage());
            return ResponseDTO.error("RESET_FAILED", "Failed to send password reset");
        }
    }

    @Override
    public ResponseDTO<String> changePassword(String email, String currentPassword, String newPassword) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                return ResponseDTO.error("INVALID_PASSWORD", "Current password is incorrect");
            }

            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            userService.logUserActivity(user, "PASSWORD_CHANGE", "auth", "Password changed", null, null);

            return ResponseDTO.success("Password changed successfully");

        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            return ResponseDTO.error("PASSWORD_CHANGE_FAILED", "Failed to change password");
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) return false;

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash())
                    .authorities("ROLE_" + user.getRole().name())
                    .build();

            return jwtService.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractEmailFromToken(String token) {
        return jwtService.extractUsername(token);
    }
    
    @Override
    public boolean isUserBlocked(String userId) {
        try {
            User user = userService.findByEmail(userId);
            return user != null && user.isBlocked();
        } catch (Exception e) {
            log.error("Error checking if user is blocked: {}", e.getMessage());
            return false;
        }
    }

    private UserDTO convertToUserDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileImageUrl(user.getProfileImageUrl())
                .phone(user.getPhone())
                .country(user.getCountry())
                .language(user.getLanguage())
                .role(user.getRole())
                .status(user.getStatus())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .isPremium(user.getIsPremium())
                .subscriptionType(user.getIsPremium() ? 
                    com.ideaspark.shared.enums.SubscriptionType.PREMIUM : 
                    com.ideaspark.shared.enums.SubscriptionType.FREE)
                .premiumExpiresAt(user.getPremiumExpiresAt())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    // Enhanced authentication methods implementation

    public ResponseDTO<Object> multiLogin(MultiLoginRequest request) {
        try {
            String identifier = request.getLoginIdentifier();
            String password = request.getPassword();
        String loginType = request.getLoginType(); // optional: "email", "phone", "otp"

        User user;

        // If loginType is 'otp', verify OTP on phone/email and skip password auth
        if (loginType != null && loginType.equalsIgnoreCase("otp")) {
        boolean verified = otpService.verifyOTP(identifier, password, "PHONE_VERIFICATION");
        if (!verified) return ResponseDTO.error("INVALID_OTP", "OTP verification failed");
        user = userRepository.findByEmailOrUsernameOrPhone(identifier)
            .orElseThrow(() -> new RuntimeException("User not found"));
        // create userDetails from user
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash())
            .authorities("ROLE_" + user.getRole().name())
            .build();

        // generate tokens below using userDetails

        } else {
        // Default: authenticate by password (identifier can be email or phone)
        user = userRepository.findByEmailOrUsernameOrPhone(identifier)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Authenticate using email (AuthenticationManager expects username)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // proceed with token generation using userDetails
        }
        // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Generate tokens
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("role", user.getRole());
            extraClaims.put("subscriptionType", user.getIsPremium() ? "PREMIUM" : "FREE");

        // Build a userDetails object for token generation
        UserDetails tokenUserDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash())
            .authorities("ROLE_" + user.getRole().name())
            .build();

        String token = jwtService.generateTokenWithClaims(tokenUserDetails, extraClaims);
        String refreshToken = jwtService.generateToken(tokenUserDetails);

            userService.logUserActivity(user, "LOGIN", "auth", "User logged in (multi)", null, null);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("user", convertToUserDTO(user));

            return ResponseDTO.success("Login successful", response);

        } catch (Exception e) {
            log.error("Error during multi-login: {}", e.getMessage());
            return ResponseDTO.error("LOGIN_FAILED", "Invalid credentials");
        }
    }
    
    @Override
    public ResponseDTO<Object> googleLogin(GoogleLoginRequest request) {
        // TODO: Implement Google OAuth login
        return ResponseDTO.<Object>builder()
                .success(false)
                .message("Google login not implemented yet")
                .build();
    }
    
    @Override
    public ResponseDTO<String> forgotPassword(ForgotPasswordRequest request) {
        try {
            // Send OTP for password reset to provided phone/email
            String phoneOrEmail = request.getPhoneOrEmail();
            boolean sent = otpService.sendOTP(phoneOrEmail, "PASSWORD_RESET");
            if (!sent) return ResponseDTO.error("OTP_SEND_FAILED", "Failed to send OTP");
            return ResponseDTO.success("Password reset OTP sent");
        } catch (Exception e) {
            log.error("Error in forgotPassword: {}", e.getMessage());
            return ResponseDTO.error("FORGOT_PASSWORD_FAILED", "Failed to process request");
        }
    }
    
    @Override
    public ResponseDTO<String> resetPasswordWithOtp(ResetPasswordRequest request) {
        try {
            String phoneOrEmail = request.getPhoneOrEmail();
            String otp = request.getOtp();
            String newPassword = request.getNewPassword();

            boolean verified = otpService.verifyOTP(phoneOrEmail, otp, "PASSWORD_RESET");
            if (!verified) return ResponseDTO.error("INVALID_OTP", "OTP verification failed");

            // Find user by email or phone and update password
            User user = userRepository.findByEmailOrUsernameOrPhone(phoneOrEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            userService.logUserActivity(user, "PASSWORD_RESET", "auth", "Password reset via OTP", null, null);

            return ResponseDTO.success("Password reset successful");
        } catch (Exception e) {
            log.error("Error in resetPasswordWithOtp: {}", e.getMessage());
            return ResponseDTO.error("PASSWORD_RESET_FAILED", "Failed to reset password");
        }
    }
    
    @Override
    public ResponseDTO<String> sendOtp(String phoneOrEmail, String purpose) {
        try {
            boolean sent = otpService.sendOTP(phoneOrEmail, purpose);
            if (!sent) return ResponseDTO.error("OTP_SEND_FAILED", "Failed to send OTP");
            return ResponseDTO.success("OTP sent successfully");
        } catch (Exception e) {
            log.error("Error in sendOtp: {}", e.getMessage());
            return ResponseDTO.error("OTP_SEND_FAILED", "Failed to send OTP");
        }
    }
    
    @Override
    public ResponseDTO<String> verifyOtp(String phoneOrEmail, String otp, String purpose) {
        try {
            boolean verified = otpService.verifyOTP(phoneOrEmail, otp, purpose);
            if (!verified) return ResponseDTO.error("INVALID_OTP", "OTP verification failed");
            return ResponseDTO.success("OTP verified successfully");
        } catch (Exception e) {
            log.error("Error in verifyOtp: {}", e.getMessage());
            return ResponseDTO.error("OTP_VERIFY_FAILED", "Failed to verify OTP");
        }
    }
    
    @Override
    public ResponseDTO<String> verifyPhoneNumber(String userId, String otp) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return ResponseDTO.error("USER_NOT_FOUND", "User not found");

            boolean verified = otpService.verifyOTP(user.getPhone(), otp, "PHONE_VERIFICATION");
            if (!verified) return ResponseDTO.error("INVALID_OTP", "OTP verification failed");

            user.setPhoneVerified(true);
            userRepository.save(user);
            userService.logUserActivity(user, "PHONE_VERIFIED", "auth", "Phone number verified", null, null);
            return ResponseDTO.success("Phone verified successfully");
        } catch (Exception e) {
            log.error("Error in verifyPhoneNumber: {}", e.getMessage());
            return ResponseDTO.error("PHONE_VERIFICATION_FAILED", "Failed to verify phone");
        }
    }

    @Override
    public ResponseDTO<Object> loginWithOtp(String phoneOrEmail, String otp) {
        try {
            boolean verified = otpService.verifyOTP(phoneOrEmail, otp, "PHONE_VERIFICATION");
            if (!verified) return ResponseDTO.error("INVALID_OTP", "OTP verification failed");

            User user = userRepository.findByEmailOrUsernameOrPhone(phoneOrEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Create tokens
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash())
                    .authorities("ROLE_" + user.getRole().name())
                    .build();

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("role", user.getRole());
            extraClaims.put("subscriptionType", user.getIsPremium() ? "PREMIUM" : "FREE");

            String token = jwtService.generateTokenWithClaims(userDetails, extraClaims);
            String refreshToken = jwtService.generateToken(userDetails);

            userService.logUserActivity(user, "LOGIN", "auth", "User logged in via OTP", null, null);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("user", convertToUserDTO(user));

            return ResponseDTO.success("Login successful", response);
        } catch (Exception e) {
            log.error("Error in loginWithOtp: {}", e.getMessage());
            return ResponseDTO.error("LOGIN_FAILED", "Failed to login with OTP");
        }
    }
    
    @Override
    public ResponseDTO<String> verifyEmail(String userId, String otp) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseDTO.error("USER_NOT_FOUND", "User not found");
            }

            String expectedOtp = user.getResetPasswordToken();
            LocalDateTime expiry = user.getResetPasswordExpiry();

            if (expectedOtp == null || expiry == null || LocalDateTime.now().isAfter(expiry)) {
                return ResponseDTO.error("OTP_EXPIRED", "OTP is expired or not present");
            }

            if (!expectedOtp.equals(otp)) {
                return ResponseDTO.error("INVALID_OTP", "Invalid OTP provided");
            }

            user.setIsVerified(true);
            user.setResetPasswordToken(null);
            user.setResetPasswordExpiry(null);
            userRepository.save(user);

            userService.logUserActivity(user, "EMAIL_VERIFIED", "auth", "Email verified", null, null);

            return ResponseDTO.success("Email verified successfully");

        } catch (Exception e) {
            log.error("Error verifying email: {}", e.getMessage());
            return ResponseDTO.error("VERIFICATION_FAILED", "Failed to verify email");
        }
    }
}