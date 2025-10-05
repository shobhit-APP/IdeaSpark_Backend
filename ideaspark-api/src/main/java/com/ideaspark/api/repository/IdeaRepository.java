package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IdeaRepository extends MongoRepository<Idea, String> {
    
    List<Idea> findByUserId(String userId);
    
    Page<Idea> findByUserId(String userId, Pageable pageable);
    
    List<Idea> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Idea> findByUserIdAndCategory(String userId, String category);
    
    List<Idea> findByUserIdAndStatus(String userId, String status);
    
    List<Idea> findByUserIdAndIsFavoriteTrue(String userId);
    
    @Query("{'userId': ?0, 'tags': {$in: ?1}}")
    List<Idea> findByUserIdAndTagsIn(String userId, List<String> tags);
    
    long countByUserId(String userId);
    
    long countByUserIdAndStatus(String userId, String status);
}