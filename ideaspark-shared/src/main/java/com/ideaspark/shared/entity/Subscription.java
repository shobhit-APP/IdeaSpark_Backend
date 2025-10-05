package com.ideaspark.shared.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ideaspark.shared.enums.SubscriptionType;

import java.time.LocalDateTime;

@Document(collection = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String subscriptionId;
    
    @Builder.Default
    private SubscriptionType type = SubscriptionType.FREE;
    
    @Builder.Default
    private String status = "active";
    
    @Builder.Default
    private LocalDateTime startsAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt;
    
    @Builder.Default
    private Boolean autoRenew = false;
    
    private String paymentMethodId;
    
    private Integer amount;
    
    private String currency = "USD";
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}