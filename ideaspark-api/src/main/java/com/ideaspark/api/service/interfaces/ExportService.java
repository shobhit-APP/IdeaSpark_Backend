package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.ResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ExportService {
    
    ResponseDTO<Map<String, Object>> exportUserData(String userEmail, String format);
    
    ResponseDTO<Map<String, Object>> exportUsageStatistics(String userEmail, LocalDateTime startDate, LocalDateTime endDate, String format);
    
    ResponseDTO<Map<String, Object>> generateCustomExport(String userEmail, String exportType, List<String> dataTypes, String format, LocalDateTime startDate, LocalDateTime endDate);
    
    ResponseDTO<byte[]> downloadExportFile(String exportId, String userEmail);
    
    ResponseDTO<Map<String, Object>> getExportStatus(String exportId, String userEmail);
    
    void cleanupOldExports();
}