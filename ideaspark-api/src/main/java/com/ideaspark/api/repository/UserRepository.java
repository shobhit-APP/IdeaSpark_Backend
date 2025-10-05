package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByGoogleId(String googleId);
    
    // Multi-login support - find by any identifier
    @Query("{ $or: [ { 'email': ?0 }, { 'username': ?0 }, { 'phone': ?0 } ] }")
    Optional<User> findByEmailOrUsernameOrPhone(String identifier);

    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhone(String phone);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    Page<User> findByRoleAndStatus(UserRole role, UserStatus status, Pageable pageable);

    long countByRole(UserRole role);

    long countByStatus(UserStatus status);

    long countByIsPremium(Boolean isPremium);
    
    List<User> findByIsPremiumTrueAndPremiumExpiresAtBefore(LocalDateTime dateTime);
    
    Optional<User> findByResetPasswordToken(String token);
    
    List<User> findByIsBlockedTrue();

    @Query("{'createdAt': {$gte: ?0}}")
    long countByCreatedAtAfter(LocalDateTime date);

    @Query("{'createdAt': {$gte: ?0, $lt: ?1}}")
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("{'lastLoginAt': {$gte: ?0}}")
    long countByLastLoginAtAfter(LocalDateTime date);
}