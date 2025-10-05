package com.ideaspark.shared.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "user_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String featureName;
    
    private String action;
    
    private String details;
    
    private String ipAddress;
    
    private String deviceInfo;
    
    private String userAgent;
    
    private String metadata; // Additional metadata as JSON string
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Alias methods for PDF export compatibility
    public String getActionType() {
        return this.action;
    }
    
    public LocalDateTime getTimestamp() {
        return this.createdAt;
    }
    
    public String getDescription() {
        return this.details;
    }
    
    public String getMetadata() {
        return this.metadata;
    }
}