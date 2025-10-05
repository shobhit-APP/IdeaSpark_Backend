package com.ideaspark.shared.exception;

public class UserBlockedException extends AuthenticationException {
    public UserBlockedException(String message) {
        super(message);
    }
    
    public UserBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
