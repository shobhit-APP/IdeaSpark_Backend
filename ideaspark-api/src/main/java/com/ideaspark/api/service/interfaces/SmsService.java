package com.ideaspark.api.service.interfaces;

public interface SmsService {
    
    /**
     * Send OTP via SMS for phone verification
     * @param phoneNumber Recipient phone number
     * @param otp OTP code
     * @return true if SMS sent successfully
     */
    boolean sendPhoneVerificationOTP(String phoneNumber, String otp);
    
    /**
     * Send OTP via SMS for password reset
     * @param phoneNumber Recipient phone number
     * @param otp OTP code
     * @return true if SMS sent successfully
     */
    boolean sendPasswordResetOTP(String phoneNumber, String otp);
    
    /**
     * Send welcome SMS to new user
     * @param phoneNumber Recipient phone number
     * @param name User's name
     * @return true if SMS sent successfully
     */
    boolean sendWelcomeSMS(String phoneNumber, String name);
    
    /**
     * Send premium subscription confirmation SMS
     * @param phoneNumber Recipient phone number
     * @param name User's name
     * @param planName Subscription plan name
     * @return true if SMS sent successfully
     */
    boolean sendPremiumSubscriptionSMS(String phoneNumber, String name, String planName);
    
    /**
     * Send account blocked notification SMS
     * @param phoneNumber Recipient phone number
     * @param name User's name
     * @return true if SMS sent successfully
     */
    boolean sendAccountBlockedSMS(String phoneNumber, String name);
    
    /**
     * Send custom SMS
     * @param phoneNumber Recipient phone number
     * @param message SMS content
     * @return true if SMS sent successfully
     */
    boolean sendCustomSMS(String phoneNumber, String message);
}