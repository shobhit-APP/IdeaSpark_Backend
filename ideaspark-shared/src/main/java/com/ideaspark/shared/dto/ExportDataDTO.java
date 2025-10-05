package com.ideaspark.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportDataDTO {
    
    private String id;
    
    private String title;
    
    private String content;
    
    private String category;
    
    private String type; // "IDEA", "CHAT", "AI_GENERATION", "ACTIVITY"
    
    private LocalDateTime createdAt;
    
    private String userId;
    
    private String userName;
    
    private String imageUrl;
    
    private String status;
    
    private String metadata; // JSON string for additional data
}