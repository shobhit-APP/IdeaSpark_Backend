package com.ideaspark.api.service.impl;

import com.ideaspark.api.repository.OtpVerificationRepository;
import com.ideaspark.api.service.interfaces.EmailService;
import com.ideaspark.api.service.interfaces.OtpService;
import com.ideaspark.api.service.interfaces.SmsService;
import com.ideaspark.shared.entity.OtpVerification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {
    
    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    
    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;
    
    @Value("${otp.max.attempts:3}")
    private int maxAttempts;
    
    private static final String EMAIL_PATTERN = 
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private static final String PHONE_PATTERN = 
            "^[+]?[1-9]\\d{1,14}$"; // E.164 format
    
    private final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
    
    @Override
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
    
    @Override
    public boolean sendOTP(String phoneOrEmail, String purpose) {
        try {
            // Clean up any existing OTP for this phone/email and purpose
            otpRepository.deleteByPhoneOrEmailAndPurpose(phoneOrEmail, purpose);
            
            // Generate new OTP
            String otp = generateOTP();
            
            // Generate OTP and attempt sending via email or SMS
            boolean sent = false;
            if (isEmail(phoneOrEmail)) {
                switch (purpose) {
                    case "PASSWORD_RESET":
                        sent = emailService.sendPasswordResetOTP(phoneOrEmail, otp, "User");
                        break;
                    case "EMAIL_VERIFICATION":
                        sent = emailService.sendEmailVerificationOTP(phoneOrEmail, otp, "User");
                        break;
                    default:
                        sent = emailService.sendCustomEmail(phoneOrEmail, "OTP Verification", 
                                "Your OTP is: " + otp);
                }
            } else if (isPhone(phoneOrEmail)) {
                switch (purpose) {
                    case "PASSWORD_RESET":
                        sent = smsService.sendPasswordResetOTP(phoneOrEmail, otp);
                        break;
                    case "PHONE_VERIFICATION":
                        sent = smsService.sendPhoneVerificationOTP(phoneOrEmail, otp);
                        break;
                    default:
                        sent = smsService.sendCustomSMS(phoneOrEmail, "Your OTP is: " + otp);
                }
            }
            if (!sent) {
                log.error("Failed to send OTP to {}", phoneOrEmail);
                return false;
            }

            // Save OTP to database only after successful send
            OtpVerification otpVerification = OtpVerification.builder()
                    .phoneOrEmail(phoneOrEmail)
                    .otp(otp)
                    .purpose(purpose)
                    .verified(false)
                    .attempts(0)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                    .build();

            try {
                // Remove any existing OTP for this phone/email+purpose to avoid duplicate key errors
                try {
                    otpRepository.deleteByPhoneOrEmailAndPurpose(phoneOrEmail, purpose);
                } catch (Exception ignore) {
                    // ignore deletion errors, we'll attempt save and log if it fails
                    log.debug("No existing OTP to delete or delete failed for {}: {}", phoneOrEmail, ignore.getMessage());
                }

                otpRepository.save(otpVerification);
                log.info("OTP sent and saved successfully to {} for purpose: {}", phoneOrEmail, purpose);
                return true;
            } catch (Exception e) {
                log.error("Failed to save OTP to DB for {}: {}", phoneOrEmail, e.getMessage(), e);
                // Optionally attempt to delete if partial state
                return false;
            }
            
        } catch (Exception e) {
            log.error("Error sending OTP to {}: {}", phoneOrEmail, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean verifyOTP(String phoneOrEmail, String otp, String purpose) {
        try {
            Optional<OtpVerification> otpVerificationOpt = 
                    otpRepository.findByPhoneOrEmailAndPurposeAndVerifiedFalse(phoneOrEmail, purpose);
            
            if (otpVerificationOpt.isEmpty()) {
                log.warn("No pending OTP found for {} with purpose {}", phoneOrEmail, purpose);
                return false;
            }
            
            OtpVerification otpVerification = otpVerificationOpt.get();
            
            // Check if OTP has expired
            if (LocalDateTime.now().isAfter(otpVerification.getExpiresAt())) {
                log.warn("OTP expired for {} with purpose {}", phoneOrEmail, purpose);
                otpRepository.delete(otpVerification);
                return false;
            }
            
            // Check attempts
            if (otpVerification.getAttempts() >= maxAttempts) {
                log.warn("Maximum OTP attempts exceeded for {} with purpose {}", phoneOrEmail, purpose);
                otpRepository.delete(otpVerification);
                return false;
            }
            
            // Increment attempts
            otpVerification.setAttempts(otpVerification.getAttempts() + 1);
            
            // Verify OTP
            if (!otp.equals(otpVerification.getOtp())) {
                otpRepository.save(otpVerification);
                log.warn("Invalid OTP provided for {} with purpose {}", phoneOrEmail, purpose);
                return false;
            }
            
            // Mark as verified
            otpVerification.setVerified(true);
            otpVerification.setVerifiedAt(LocalDateTime.now());
            otpRepository.save(otpVerification);
            
            log.info("OTP verified successfully for {} with purpose {}", phoneOrEmail, purpose);
            return true;
            
        } catch (Exception e) {
            log.error("Error verifying OTP for {}: {}", phoneOrEmail, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isValidPhoneOrEmail(String phoneOrEmail) {
        return isEmail(phoneOrEmail) || isPhone(phoneOrEmail);
    }
    
    @Override
    public void cleanupExpiredOTPs() {
        try {
            otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
            log.info("Cleaned up expired OTPs");
        } catch (Exception e) {
            log.error("Error cleaning up expired OTPs: {}", e.getMessage());
        }
    }
    
    private boolean isEmail(String input) {
        return emailPattern.matcher(input).matches();
    }
    
    private boolean isPhone(String input) {
        // Remove any spaces, dashes, or parentheses
        String cleanInput = input.replaceAll("[\\s\\-\\(\\)]", "");
        return phonePattern.matcher(cleanInput).matches();
    }
}