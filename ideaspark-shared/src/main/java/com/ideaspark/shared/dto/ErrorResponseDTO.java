package com.ideaspark.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String exception;
    private String trace;
    private Map<String, Object> details;
    private List<FieldErrorDTO> fieldErrors;
    
    public static ErrorResponseDTO of(String error, String message, String path, int status) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
    
    public static ErrorResponseDTO of(String error, String message, String path, int status, String exception) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .exception(exception)
                .build();
    }
}