package com.ideaspark.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ideas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Idea {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String title;
    
    private String description;
    
    private String category;
    
    private List<String> tags;
    
    private String priority; // "HIGH", "MEDIUM", "LOW"
    
    private String status; // "DRAFT", "ACTIVE", "COMPLETED", "ARCHIVED"
    
    private String imageUrl;
    
    private List<String> attachmentUrls;
    
    private String aiGenerated; // Source AI if generated
    
    private String prompt; // Original prompt if AI generated
    
    private int likes;
    
    private int views;
    
    private boolean isPublic;
    
    private boolean isFavorite;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
}