package com.ideaspark.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;

@Document(collection = "otps")
@CompoundIndexes({
    @CompoundIndex(name = "phone_purpose_idx", def = "{ 'phoneOrEmail': 1, 'purpose': 1}", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {
    
    @Id
    private String id;
    
    @Indexed
    private String phoneOrEmail;
    
    private String otp;
    
    private String purpose; // "PASSWORD_RESET", "PHONE_VERIFICATION", "EMAIL_VERIFICATION"
    
    private boolean verified;
    
    private int attempts;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt;
    
    private LocalDateTime verifiedAt;
}