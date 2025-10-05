package com.ideaspark.shared.exception;

public class CloudinaryException extends ServiceException {
    public CloudinaryException(String message) {
        super(message);
    }
    
    public CloudinaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
