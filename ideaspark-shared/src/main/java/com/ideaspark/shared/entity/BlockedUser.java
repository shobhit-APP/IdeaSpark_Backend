package com.ideaspark.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "blocked_users", timeToLive = 86400) // 24 hours default TTL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockedUser {
    
    @Id
    private String userId;
    
    private String reason;
    
    private String blockedByAdminId;
    
    private boolean permanent;
    
    private LocalDateTime blockedAt;
    
    private LocalDateTime unblockAt; // For temporary blocks
}