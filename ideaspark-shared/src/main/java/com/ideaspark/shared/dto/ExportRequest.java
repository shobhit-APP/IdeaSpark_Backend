package com.ideaspark.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    
    private String exportType; // "PDF", "EXCEL", "CSV", "JSON"
    
    private String dataType; // "IDEAS", "CHAT_HISTORY", "USER_ACTIVITY", "AI_GENERATIONS", "ALL"
    
    private String dateFrom; // ISO date string
    
    private String dateTo; // ISO date string
    
    private List<String> includeFields; // Specific fields to include
    
    private boolean includeImages; // Whether to include images in export
    
    private String format; // "DETAILED", "SUMMARY", "MINIMAL"
    
    private String templateStyle; // "MODERN", "CLASSIC", "MINIMAL"
}