package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    Page<UserActivity> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<UserActivity> findByUserAndActionOrderByCreatedAtDesc(User user, String action);

    @Query("{'user': ?0, 'createdAt': {$gte: ?1, $lt: ?2}}")
    List<UserActivity> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query("{'featureName': ?0, 'createdAt': {$gte: ?1}}")
    long countByFeatureNameAndCreatedAtAfter(String featureName, LocalDateTime date);

    @Query("{'action': ?0, 'createdAt': {$gte: ?1, $lt: ?2}}")
    long countByActionAndCreatedAtBetween(String action, LocalDateTime start, LocalDateTime end);

    // Add count method for user ID
    @Query("{'user._id': ?0}")
    long countByUserId(String userId);

    // Add find method for user ID (for PDF export)
    @Query("{'user._id': ?0}")
    List<UserActivity> findByUserId(String userId);

    @Query("{'user._id': ?0}")
    Page<UserActivity> findByUserId(String userId, Pageable pageable);

    // Add methods for finding by user ID and timestamp range
    @Query("{'user._id': ?0, 'createdAt': {$gte: ?1, $lt: ?2}}")
    List<UserActivity> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);

    // Add method for finding by user ID ordered by timestamp
    @Query("{'user._id': ?0}")
    List<UserActivity> findByUserIdOrderByTimestampDesc(String userId);

    void deleteByUserAndCreatedAtBefore(User user, LocalDateTime date);
}