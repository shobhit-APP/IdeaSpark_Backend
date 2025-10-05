package com.ideaspark.shared.exception;

public class EmailServiceException extends ServiceException {
    public EmailServiceException(String message) {
        super(message);
    }
    
    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
