package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.BlockUserRequest;
import com.ideaspark.shared.dto.ResponseDTO;

import java.util.List;

public interface UserBlockingService {
    
    /**
     * Block a user
     * @param request Block user request details
     * @param adminId ID of admin performing the action
     * @return ResponseDTO with operation result
     */
    ResponseDTO<String> blockUser(BlockUserRequest request, String adminId);
    
    /**
     * Unblock a user
     * @param userId User ID to unblock
     * @param adminId ID of admin performing the action
     * @return ResponseDTO with operation result
     */
    ResponseDTO<String> unblockUser(String userId, String adminId);
    
    /**
     * Check if user is currently blocked
     * @param userId User ID to check
     * @return true if user is blocked
     */
    boolean isUserBlocked(String userId);
    
    /**
     * Get all blocked users
     * @return List of blocked user IDs with details
     */
    ResponseDTO<List<Object>> getAllBlockedUsers();
    
    /**
     * Get blocking details for a specific user
     * @param userId User ID to get details for
     * @return Blocking details if user is blocked
     */
    ResponseDTO<Object> getBlockingDetails(String userId);
    
    /**
     * Auto-unblock users whose temporary block has expired
     * This method should be called periodically by a scheduler
     */
    void autoUnblockExpiredUsers();
}