package com.ideaspark.api.exception;

import com.ideaspark.shared.dto.ErrorResponseDTO;
import com.ideaspark.shared.dto.FieldErrorDTO;
import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Global Exception Handler for the IdeaSpark Application
 * Provides centralized error handling and standardized error responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // =================================================================================
    // CUSTOM APPLICATION EXCEPTIONS
    // =================================================================================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO<Object>> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("User not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDTO.error("USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(com.ideaspark.shared.exception.AuthenticationException.class)
    public ResponseEntity<ResponseDTO<Object>> handleAuthenticationException(
            com.ideaspark.shared.exception.AuthenticationException ex, HttpServletRequest request) {
        log.error("Authentication error: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDTO.error("AUTHENTICATION_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ResponseDTO<Object>> handleUserBlockedException(
            UserBlockedException ex, HttpServletRequest request) {
        log.error("User blocked: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseDTO.error("USER_BLOCKED", ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO<Object>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error("VALIDATION_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseDTO<Object>> handleServiceException(
            ServiceException ex, HttpServletRequest request) {
        log.error("Service error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.error("SERVICE_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<ResponseDTO<Object>> handleEmailServiceException(
            EmailServiceException ex, HttpServletRequest request) {
        log.error("Email service error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ResponseDTO.error("EMAIL_SERVICE_ERROR", 
                    "Email service is currently unavailable. Please try again later."));
    }

    @ExceptionHandler(SmsServiceException.class)
    public ResponseEntity<ResponseDTO<Object>> handleSmsServiceException(
            SmsServiceException ex, HttpServletRequest request) {
        log.error("SMS service error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ResponseDTO.error("SMS_SERVICE_ERROR", 
                    "SMS service is currently unavailable. Please try again later."));
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<ResponseDTO<Object>> handleCloudinaryException(
            CloudinaryException ex, HttpServletRequest request) {
        log.error("Cloudinary service error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ResponseDTO.error("IMAGE_SERVICE_ERROR", 
                    "Image service is currently unavailable. Please try again later."));
    }

    @ExceptionHandler(ExportException.class)
    public ResponseEntity<ResponseDTO<Object>> handleExportException(
            ExportException ex, HttpServletRequest request) {
        log.error("Export error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.error("EXPORT_ERROR", ex.getMessage()));
    }

    // =================================================================================
    // SPRING SECURITY EXCEPTIONS
    // =================================================================================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO<Object>> handleSpringAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        log.error("Spring Authentication error: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDTO.error("AUTHENTICATION_FAILED", "Invalid credentials"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO<Object>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        log.error("Bad credentials: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDTO.error("BAD_CREDENTIALS", "Invalid username or password"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseDTO.error("ACCESS_DENIED", "You don't have permission to access this resource"));
    }

    // =================================================================================
    // VALIDATION EXCEPTIONS
    // =================================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(FieldErrorDTO.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .rejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : null)
                    .build());
        }
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .error("VALIDATION_FAILED")
                .message("Validation failed for one or more fields")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.<Object>builder()
                        .success(false)
                        .error(ResponseDTO.ErrorDetails.builder()
                                .code("VALIDATION_FAILED")
                                .message("Validation failed for one or more fields")
                                .details(errorResponse.toString())
                                .build())
                        .build());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDTO<Object>> handleBindException(
            BindException ex, HttpServletRequest request) {
        log.error("Binding error: {}", ex.getMessage());
        
        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(FieldErrorDTO.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .rejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : null)
                    .build());
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error("BIND_ERROR", "Data binding failed"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDTO<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Constraint violation: {}", ex.getMessage());
        
        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            fieldErrors.add(FieldErrorDTO.builder()
                    .field(violation.getPropertyPath().toString())
                    .message(violation.getMessage())
                    .rejectedValue(violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : null)
                    .build());
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error("CONSTRAINT_VIOLATION", "Constraint validation failed"));
    }

    // =================================================================================
    // HTTP AND REQUEST EXCEPTIONS
    // =================================================================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDTO<Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.error("Method not supported: {}", ex.getMessage());
        
        String supportedMethods = String.join(", ", ex.getSupportedMethods());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseDTO.error("METHOD_NOT_ALLOWED", 
                    "Request method '" + ex.getMethod() + "' not supported. Supported methods: " + supportedMethods));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.error("Missing request parameter: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error("MISSING_PARAMETER", 
                    "Required parameter '" + ex.getParameterName() + "' is missing"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Type mismatch: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error("TYPE_MISMATCH", 
                    "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType().getSimpleName()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("File size exceeded: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ResponseDTO.error("FILE_SIZE_EXCEEDED", 
                    "File size exceeds the maximum allowed limit"));
    }

    // =================================================================================
    // GENERAL EXCEPTIONS
    // =================================================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.error("ILLEGAL_ARGUMENT", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDTO<Object>> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        log.error("Illegal state: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDTO.error("ILLEGAL_STATE", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDTO<Object>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.error("RUNTIME_ERROR", "An unexpected error occurred"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}