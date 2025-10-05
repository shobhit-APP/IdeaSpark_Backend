package com.ideaspark.shared.dto;

import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;
import com.ideaspark.shared.enums.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private String id;
    private String email;
    private String fullName;
    private String profileImageUrl;
    private String phone;
    private String country;
    private String language;
    private UserRole role;
    private UserStatus status;
    private Boolean isActive;
    private Boolean isVerified;
    private Boolean isPremium;
    private SubscriptionType subscriptionType;
    private LocalDateTime premiumExpiresAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}