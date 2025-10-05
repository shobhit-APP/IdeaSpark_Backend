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
public class MultiLoginRequest {
    
    @NotBlank(message = "Login identifier is required (username, email, or phone)")
    private String loginIdentifier; // Can be username, email, or phone
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private String loginType; // "username", "email", "phone" - optional, will be auto-detected
}