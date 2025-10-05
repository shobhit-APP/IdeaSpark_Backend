package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.ResponseDTO;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for error reporting and analytics
 */
public interface ErrorReportService {
    
    /**
     * Report an error occurrence
     */
    void reportError(String errorType, String message, String stackTrace, String userId, String endpoint);
    
    /**
     * Get error statistics for admin dashboard
     */
    ResponseDTO<Map<String, Object>> getErrorStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get most common errors
     */
    ResponseDTO<Map<String, Object>> getMostCommonErrors(int limit);
    
    /**
     * Get error trends over time
     */
    ResponseDTO<Map<String, Object>> getErrorTrends(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Clear old error reports
     */
    void cleanOldReports(int daysToKeep);
}