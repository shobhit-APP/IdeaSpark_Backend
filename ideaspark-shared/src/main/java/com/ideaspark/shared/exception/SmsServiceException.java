package com.ideaspark.shared.exception;

public class SmsServiceException extends ServiceException {
    public SmsServiceException(String message) {
        super(message);
    }
    
    public SmsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
