package com.ideaspark.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "ai_generations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGeneration {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String type; // "CHAT", "WRITER", "CODE", "IMAGE", "IDEA", "NEWS_DETECTION", "TEXT_TOOLS", "VOICE_TOOLS"
    
    private String prompt;
    
    private String response;
    
    private String model; // "GPT", "GEMINI", "CEREBRAS", etc.
    
    private String sessionId; // For chat conversations
    
    private String language;
    
    private int tokenUsed;
    
    private String imageUrl; // For image generations
    
    private String voiceUrl; // For voice generations
    
    private String metadata; // JSON string for additional data
    
    private boolean isBookmarked;
    
    private boolean isShared;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}