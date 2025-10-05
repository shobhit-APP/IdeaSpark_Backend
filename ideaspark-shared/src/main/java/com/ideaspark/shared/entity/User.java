package com.ideaspark.shared.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    @Email
    private String email;
    
    private String passwordHash;
    
    private String firstName;
    
    private String lastName;
    
    private String fullName;
    
    private String profileImageUrl; // Updated field name
    
    @Indexed(unique = true)
    private String phone;
    
    private LocalDateTime dateOfBirth;
    
    private String country;
    
    private String timezone;
    
    // New fields for enhanced authentication
    private String googleId; // For Google OAuth
    
    private boolean phoneVerified;
    
    private boolean emailVerified;
    
    private String resetPasswordToken;
    
    private LocalDateTime resetPasswordExpiry;
    
    // Blocking functionality
    private boolean isBlocked;
    
    private LocalDateTime blockedAt;
    
    private String blockedReason;
    
    private String blockedByAdminId;
    
    @Builder.Default
    private String language = "en";
    
    @Builder.Default
    private UserRole role = UserRole.USER;
    
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean isVerified = false;
    
    @Builder.Default
    private Boolean isAdmin = false;
    
    @Builder.Default
    private Boolean isPremium = false;
    
    private LocalDateTime premiumExpiresAt;
    
    private LocalDateTime lastLoginAt;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}