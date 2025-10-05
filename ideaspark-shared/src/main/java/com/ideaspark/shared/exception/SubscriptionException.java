package com.ideaspark.shared.exception;

public class SubscriptionException extends RuntimeException {
    public SubscriptionException(String message) {
        super(message);
    }
    
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
