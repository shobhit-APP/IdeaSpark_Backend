package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends MongoRepository<OtpVerification, String> {
    
    @Query("{ 'phoneOrEmail' : ?0, 'purpose' : ?1, 'verified' : false }")
    Optional<OtpVerification> findByPhoneOrEmailAndPurposeAndVerifiedFalse(String phoneOrEmail, String purpose);
    
    @Query("{ 'phoneOrEmail' : ?0, 'otp' : ?1, 'purpose' : ?2, 'verified' : false }")
    Optional<OtpVerification> findByPhoneOrEmailAndOtpAndPurposeAndVerifiedFalse(String phoneOrEmail, String otp, String purpose);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    @Query(value = "{ 'phoneOrEmail' : ?0, 'purpose' : ?1 }", delete = true)
    void deleteByPhoneOrEmailAndPurpose(String phoneOrEmail, String purpose);
}