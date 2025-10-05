package com.ideaspark.shared.exception;

public class ExportException extends ServiceException {
    public ExportException(String message) {
        super(message);
    }
    
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
