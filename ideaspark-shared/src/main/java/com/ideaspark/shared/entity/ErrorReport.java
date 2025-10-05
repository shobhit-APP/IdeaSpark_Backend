package com.ideaspark.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "error_reports")
public class ErrorReport {
    
    @Id
    private String id;
    
    private String errorType;
    private String message;
    private String stackTrace;
    private String userId;
    private String endpoint;
    private String httpMethod;
    private String userAgent;
    private String ipAddress;
    private LocalDateTime timestamp;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private boolean resolved;
    private String resolution;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
}