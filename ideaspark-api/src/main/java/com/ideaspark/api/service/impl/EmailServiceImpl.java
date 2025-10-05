package com.ideaspark.api.service.impl;

import com.ideaspark.api.exception.ExceptionUtils;
import com.ideaspark.api.service.interfaces.EmailService;
import com.ideaspark.shared.exception.EmailServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Override
    public boolean sendWelcomeEmail(String to, String name) {
        try {
            String subject = "Welcome to IdeaSpark! 🎉";
            String content = buildWelcomeEmailContent(name);
            return sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            log.error("Error sending welcome email to {}: {}", to, e.getMessage());
            ExceptionUtils.logAndThrowEmailError("welcome email sending", e);
            return false; // Never reached due to exception, but required for compilation
        }
    }
    
    @Override
    public boolean sendEmailVerificationOTP(String to, String otp, String name) {
        try {
            String subject = "Verify Your Email - IdeaSpark";
            String content = buildEmailVerificationContent(name, otp);
            return sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            log.error("Error sending email verification OTP to {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendPasswordResetOTP(String to, String otp, String name) {
        try {
            String subject = "Password Reset OTP - IdeaSpark";
            String content = buildPasswordResetContent(name, otp);
            return sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            log.error("Error sending password reset OTP to {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendPremiumSubscriptionEmail(String to, String name, String planName) {
        try {
            String subject = "Welcome to IdeaSpark Premium! ⭐";
            String content = buildPremiumSubscriptionContent(name, planName);
            return sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            log.error("Error sending premium subscription email to {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendAccountBlockedEmail(String to, String name, String reason) {
        try {
            String subject = "Account Status Update - IdeaSpark";
            String content = buildAccountBlockedContent(name, reason);
            return sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            log.error("Error sending account blocked email to {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendCustomEmail(String to, String subject, String content) {
        try {
            return sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            log.error("Error sending custom email to {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    private boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@ideaspark.com");
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return true;
            
        } catch (MessagingException e) {
            log.error("Error sending HTML email to {}: {}", to, e.getMessage());
            return false;
        }
    }
    
    private String buildWelcomeEmailContent(String name) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #4F46E5;'>Welcome to IdeaSpark! 🎉</h1>" +
                "<p>Hi " + name + ",</p>" +
                "<p>Welcome to IdeaSpark! We're excited to have you on board.</p>" +
                "<p>Start exploring our features and unlock your creativity:</p>" +
                "<ul>" +
                "<li>💡 Generate innovative ideas</li>" +
                "<li>📊 Track your progress</li>" +
                "<li>⭐ Upgrade to Premium for unlimited access</li>" +
                "</ul>" +
                "<p>If you have any questions, feel free to reach out to our support team.</p>" +
                "<p>Best regards,<br>The IdeaSpark Team</p>" +
                "</div></body></html>";
    }
    
    private String buildEmailVerificationContent(String name, String otp) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #4F46E5;'>Verify Your Email</h1>" +
                "<p>Hi " + name + ",</p>" +
                "<p>Please use the following OTP to verify your email address:</p>" +
                "<div style='background: #F3F4F6; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;'>" +
                "<h2 style='color: #1F2937; margin: 0; font-size: 32px; letter-spacing: 8px;'>" + otp + "</h2>" +
                "</div>" +
                "<p>This OTP will expire in 5 minutes.</p>" +
                "<p>If you didn't request this verification, please ignore this email.</p>" +
                "<p>Best regards,<br>The IdeaSpark Team</p>" +
                "</div></body></html>";
    }
    
    private String buildPasswordResetContent(String name, String otp) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #4F46E5;'>Password Reset Request</h1>" +
                "<p>Hi " + name + ",</p>" +
                "<p>You requested to reset your password. Please use the following OTP:</p>" +
                "<div style='background: #FEF2F2; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0; border: 2px solid #FCA5A5;'>" +
                "<h2 style='color: #DC2626; margin: 0; font-size: 32px; letter-spacing: 8px;'>" + otp + "</h2>" +
                "</div>" +
                "<p>This OTP will expire in 5 minutes.</p>" +
                "<p>If you didn't request this password reset, please ignore this email and ensure your account is secure.</p>" +
                "<p>Best regards,<br>The IdeaSpark Team</p>" +
                "</div></body></html>";
    }
    
    private String buildPremiumSubscriptionContent(String name, String planName) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #F59E0B;'>Welcome to IdeaSpark Premium! ⭐</h1>" +
                "<p>Hi " + name + ",</p>" +
                "<p>Congratulations! You've successfully subscribed to " + planName + ".</p>" +
                "<p>You now have access to premium features:</p>" +
                "<ul>" +
                "<li>🚀 Unlimited idea generation</li>" +
                "<li>📈 Advanced analytics</li>" +
                "<li>📁 Export capabilities</li>" +
                "<li>🎯 Priority support</li>" +
                "</ul>" +
                "<p>Start exploring your premium features now!</p>" +
                "<p>Thank you for choosing IdeaSpark Premium!</p>" +
                "<p>Best regards,<br>The IdeaSpark Team</p>" +
                "</div></body></html>";
    }
    
    private String buildAccountBlockedContent(String name, String reason) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #DC2626;'>Account Status Update</h1>" +
                "<p>Hi " + name + ",</p>" +
                "<p>We're writing to inform you that your IdeaSpark account has been temporarily suspended.</p>" +
                "<p><strong>Reason:</strong> " + reason + "</p>" +
                "<p>If you believe this action was taken in error, please contact our support team for assistance.</p>" +
                "<p>Email: support@ideaspark.com</p>" +
                "<p>We appreciate your understanding.</p>" +
                "<p>Best regards,<br>The IdeaSpark Team</p>" +
                "</div></body></html>";
    }
}