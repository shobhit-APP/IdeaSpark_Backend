package com.ideaspark.api.repository;

import com.ideaspark.shared.entity.BlockedUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockedUserRepository extends CrudRepository<BlockedUser, String> {
    
    Optional<BlockedUser> findByUserId(String userId);
    
    List<BlockedUser> findByPermanentFalseAndUnblockAtBefore(LocalDateTime dateTime);
    
    void deleteByUserId(String userId);
}