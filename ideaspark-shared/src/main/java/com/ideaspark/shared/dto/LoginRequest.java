package com.ideaspark.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Email or phone is required")
    private String identifier; // email or phone
    
    @NotBlank(message = "Password is required")
    private String password;

    private String loginType; // optional: "email" or "phone"
}