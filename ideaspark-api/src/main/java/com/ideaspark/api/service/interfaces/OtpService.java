package com.ideaspark.api.service.interfaces;

public interface OtpService {
    
    /**
     * Generate OTP
     * @return 6-digit OTP string
     */
    String generateOTP();
    
    /**
     * Send OTP via email or SMS
     * @param phoneOrEmail Phone number or email address
     * @param purpose Purpose of OTP (PASSWORD_RESET, PHONE_VERIFICATION, EMAIL_VERIFICATION)
     * @return true if OTP sent successfully
     */
    boolean sendOTP(String phoneOrEmail, String purpose);
    
    /**
     * Verify OTP
     * @param phoneOrEmail Phone number or email address
     * @param otp OTP to verify
     * @param purpose Purpose of OTP
     * @return true if OTP is valid and verified
     */
    boolean verifyOTP(String phoneOrEmail, String otp, String purpose);
    
    /**
     * Check if phone number or email is valid
     * @param phoneOrEmail Phone number or email to validate
     * @return true if valid
     */
    boolean isValidPhoneOrEmail(String phoneOrEmail);
    
    /**
     * Clean up expired OTPs
     * This method should be called periodically by a scheduler
     */
    void cleanupExpiredOTPs();
}