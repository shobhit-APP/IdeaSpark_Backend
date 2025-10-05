package com.ideaspark.api.service.interfaces;

public interface EmailService {
    
    /**
     * Send welcome email to new user
     * @param to Recipient email address
     * @param name User's name
     * @return true if email sent successfully
     */
    boolean sendWelcomeEmail(String to, String name);
    
    /**
     * Send OTP for email verification
     * @param to Recipient email address
     * @param otp OTP code
     * @param name User's name
     * @return true if email sent successfully
     */
    boolean sendEmailVerificationOTP(String to, String otp, String name);
    
    /**
     * Send password reset OTP
     * @param to Recipient email address
     * @param otp OTP code
     * @param name User's name
     * @return true if email sent successfully
     */
    boolean sendPasswordResetOTP(String to, String otp, String name);
    
    /**
     * Send premium subscription confirmation email
     * @param to Recipient email address
     * @param name User's name
     * @param planName Subscription plan name
     * @return true if email sent successfully
     */
    boolean sendPremiumSubscriptionEmail(String to, String name, String planName);
    
    /**
     * Send account blocked notification
     * @param to Recipient email address
     * @param name User's name
     * @param reason Blocking reason
     * @return true if email sent successfully
     */
    boolean sendAccountBlockedEmail(String to, String name, String reason);
    
    /**
     * Send generic email with custom template
     * @param to Recipient email address
     * @param subject Email subject
     * @param content Email content (HTML)
     * @return true if email sent successfully
     */
    boolean sendCustomEmail(String to, String subject, String content);
}