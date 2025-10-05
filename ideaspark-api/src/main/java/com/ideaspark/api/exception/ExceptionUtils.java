package com.ideaspark.api.exception;

import com.ideaspark.shared.exception.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for exception handling operations
 */
@UtilityClass
@Slf4j
public class ExceptionUtils {

    // =================================================================================
    // USER RELATED EXCEPTIONS
    // =================================================================================

    public static RuntimeException throwUserNotFound(String identifier) {
        throw new UserNotFoundException("User not found with identifier: " + identifier);
    }

    public static RuntimeException throwUserNotFoundById(String userId) {
        throw new UserNotFoundException("User not found with ID: " + userId);
    }

    public static RuntimeException throwUserNotFoundByEmail(String email) {
        throw new UserNotFoundException("User not found with email: " + email);
    }

    public static RuntimeException throwUserBlocked(String reason) {
        throw new UserBlockedException("Your account has been blocked. Reason: " + reason);
    }

    public static RuntimeException throwUserBlocked() {
        throw new UserBlockedException("Your account has been blocked. Please contact support.");
    }

    // =================================================================================
    // AUTHENTICATION EXCEPTIONS
    // =================================================================================

    public static RuntimeException throwInvalidCredentials() {
        throw new AuthenticationException("Invalid username or password");
    }

    public static void throwTokenExpired() {
        throw new AuthenticationException("Authentication token has expired");
    }

    public static void throwInvalidToken() {
        throw new AuthenticationException("Invalid authentication token");
    }

    public static void throwUnauthorized(String message) {
        throw new AuthenticationException(message);
    }

    // =================================================================================
    // VALIDATION EXCEPTIONS
    // =================================================================================

    public static void throwValidationError(String message) {
        throw new ValidationException(message);
    }

    public static void throwInvalidEmail() {
        throw new ValidationException("Invalid email format");
    }

    public static void throwInvalidPhoneNumber() {
        throw new ValidationException("Invalid phone number format");
    }

    public static void throwPasswordTooWeak() {
        throw new ValidationException("Password must be at least 8 characters long and contain uppercase, lowercase, number and special character");
    }

    public static void throwInvalidFileFormat(String allowedFormats) {
        throw new ValidationException("Invalid file format. Allowed formats: " + allowedFormats);
    }

    public static void throwFileSizeExceeded(String maxSize) {
        throw new ValidationException("File size exceeds the maximum allowed limit of " + maxSize);
    }

    // =================================================================================
    // SERVICE EXCEPTIONS
    // =================================================================================

    public static void throwEmailServiceError(String message, Throwable cause) {
        throw new EmailServiceException("Email service error: " + message, cause);
    }

    public static void throwSmsServiceError(String message, Throwable cause) {
        throw new SmsServiceException("SMS service error: " + message, cause);
    }

    public static void throwCloudinaryError(String message, Throwable cause) {
        throw new CloudinaryException("Image service error: " + message, cause);
    }

    public static void throwPdfGenerationError(String message, Throwable cause) {
        throw new PdfGenerationException("PDF generation error: " + message, cause);
    }

    public static void throwExportError(String message, Throwable cause) {
        throw new ExportException("Export error: " + message, cause);
    }

    // =================================================================================
    // OTP EXCEPTIONS
    // =================================================================================

    public static void throwInvalidOtp() {
        throw new OtpException("Invalid or expired OTP");
    }

    public static void throwOtpExpired() {
        throw new OtpException("OTP has expired. Please request a new one");
    }

    public static void throwOtpAlreadyVerified() {
        throw new OtpException("OTP has already been verified");
    }

    public static void throwTooManyOtpAttempts() {
        throw new OtpException("Too many OTP attempts. Please wait before requesting a new one");
    }

    public static void throwOtpGenerationFailed() {
        throw new OtpException("Failed to generate OTP. Please try again");
    }

    // =================================================================================
    // SUBSCRIPTION EXCEPTIONS
    // =================================================================================

    public static void throwSubscriptionNotFound() {
        throw new SubscriptionException("Subscription not found");
    }

    public static void throwSubscriptionExpired() {
        throw new SubscriptionException("Your subscription has expired. Please renew to continue using premium features");
    }

    public static void throwSubscriptionAlreadyActive() {
        throw new SubscriptionException("You already have an active subscription");
    }

    public static void throwInsufficientPermissions() {
        throw new SubscriptionException("This feature requires a premium subscription. Please upgrade to continue");
    }

    public static void throwPaymentFailed(String reason) {
        throw new SubscriptionException("Payment failed: " + reason);
    }

    // =================================================================================
    // UTILITY METHODS
    // =================================================================================

    public static void throwIfNull(Object object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    public static void throwIfEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void throwIfBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
    }

    public static void throwIfTrue(boolean condition, String message) {
        if (condition) {
            throw new ValidationException(message);
        }
    }

    public static void throwIfFalse(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    public static void logAndThrowServiceError(String operation, Exception e) {
        log.error("Service error during {}: {}", operation, e.getMessage(), e);
        throw new ServiceException("Operation failed: " + operation, e);
    }

    public static void logAndThrowEmailError(String operation, Exception e) {
        log.error("Email service error during {}: {}", operation, e.getMessage(), e);
        throw new EmailServiceException("Email operation failed: " + operation, e);
    }

    public static void logAndThrowSmsError(String operation, Exception e) {
        log.error("SMS service error during {}: {}", operation, e.getMessage(), e);
        throw new SmsServiceException("SMS operation failed: " + operation, e);
    }

    public static void logAndThrowCloudinaryError(String operation, Exception e) {
        log.error("Cloudinary service error during {}: {}", operation, e.getMessage(), e);
        throw new CloudinaryException("Image operation failed: " + operation, e);
    }
}