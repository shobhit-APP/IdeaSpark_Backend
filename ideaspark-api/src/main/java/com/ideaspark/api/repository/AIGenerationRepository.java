package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.AIGeneration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AIGenerationRepository extends MongoRepository<AIGeneration, String> {
    
    List<AIGeneration> findByUserId(String userId);
    
    Page<AIGeneration> findByUserId(String userId, Pageable pageable);
    
    List<AIGeneration> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<AIGeneration> findByUserIdAndType(String userId, String type);
    
    List<AIGeneration> findByUserIdAndSessionId(String userId, String sessionId);
    
    List<AIGeneration> findByUserIdAndIsBookmarkedTrue(String userId);
    
    long countByUserId(String userId);
    
    long countByUserIdAndType(String userId, String type);
    
    List<AIGeneration> findByUserIdOrderByCreatedAtDesc(String userId);
}