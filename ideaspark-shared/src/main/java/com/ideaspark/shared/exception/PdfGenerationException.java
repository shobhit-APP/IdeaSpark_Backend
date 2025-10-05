package com.ideaspark.shared.exception;

public class PdfGenerationException extends ServiceException {
    public PdfGenerationException(String message) {
        super(message);
    }
    
    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
