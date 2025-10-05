package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.Subscription;
import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.enums.SubscriptionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Optional<Subscription> findByUserAndStatus(User user, String status);

    List<Subscription> findByUserOrderByCreatedAtDesc(User user);

    Optional<Subscription> findBySubscriptionId(String subscriptionId);

    List<Subscription> findByStatus(String status);

    List<Subscription> findByType(SubscriptionType type);

    long countByType(SubscriptionType type);

    long countByStatus(String status);

    @Query("{'expiresAt': {$lt: ?0}, 'status': 'active'}")
    List<Subscription> findExpiredActiveSubscriptions(LocalDateTime now);

    @Query("{'expiresAt': {$gte: ?0, $lt: ?1}, 'status': 'active'}")
    List<Subscription> findExpiringSubscriptions(LocalDateTime start, LocalDateTime end);
}