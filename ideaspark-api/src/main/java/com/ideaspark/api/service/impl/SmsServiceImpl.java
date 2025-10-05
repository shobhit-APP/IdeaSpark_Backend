package com.ideaspark.api.service.impl;

import com.ideaspark.api.service.interfaces.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {
    
    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;
    
    @Override
    public boolean sendPhoneVerificationOTP(String phoneNumber, String otp) {
        try {
            String message = "Your IdeaSpark phone verification OTP is: " + otp + 
                           ". This code will expire in 5 minutes. Do not share this code with anyone.";
            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            log.error("Error sending phone verification OTP to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendPasswordResetOTP(String phoneNumber, String otp) {
        try {
            String message = "Your IdeaSpark password reset OTP is: " + otp + 
                           ". This code will expire in 5 minutes. If you didn't request this, please ignore.";
            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            log.error("Error sending password reset OTP to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendWelcomeSMS(String phoneNumber, String name) {
        try {
            String message = "Hi " + name + "! Welcome to IdeaSpark! 🎉 " +
                           "Start generating amazing ideas today. Download our app and get started!";
            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            log.error("Error sending welcome SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendPremiumSubscriptionSMS(String phoneNumber, String name, String planName) {
        try {
            String message = "Hi " + name + "! 🎊 Welcome to IdeaSpark " + planName + "! " +
                           "You now have access to premium features. Enjoy unlimited creativity!";
            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            log.error("Error sending premium subscription SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendAccountBlockedSMS(String phoneNumber, String name) {
        try {
            String message = "Hi " + name + ", your IdeaSpark account has been temporarily suspended. " +
                           "Please contact support for assistance: support@ideaspark.com";
            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            log.error("Error sending account blocked SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendCustomSMS(String phoneNumber, String message) {
        try {
            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            log.error("Error sending custom SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    private boolean sendSMS(String toPhoneNumber, String messageBody) {
        try {
            // Ensure phone number is in E.164 format
            if (!toPhoneNumber.startsWith("+")) {
                toPhoneNumber = "+91" + toPhoneNumber; // Default to India country code
            }
            
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody
            ).create();
            
            log.info("SMS sent successfully to {}. Message SID: {}", toPhoneNumber, message.getSid());
            return true;
            
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", toPhoneNumber, e.getMessage());
            return false;
        }
    }
}