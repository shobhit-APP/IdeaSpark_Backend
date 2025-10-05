package com.ideaspark.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockUserRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    private String reason;
    
    private boolean permanent;
    
    private int durationHours; // For temporary blocks
}