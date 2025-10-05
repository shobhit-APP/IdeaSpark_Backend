package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.dto.UserDTO;
import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface UserService {
    
    ResponseDTO<UserDTO> getUserProfile(String email);
    
    ResponseDTO<UserDTO> updateUserProfile(String email, UserDTO userDTO);
    
    ResponseDTO<Map<String, Object>> getAllUsers(int page, int size, UserRole role, UserStatus status);
    
    ResponseDTO<String> updateUserStatus(String userId, UserStatus status);
    
    ResponseDTO<String> deleteUser(String userId);
    
    ResponseDTO<Map<String, Object>> getUserStatistics();
    
    ResponseDTO<Page<Map<String, Object>>> getUserActivity(String email, int limit);
    
    User findByEmail(String email);
    
    User saveUser(User user);
    
    boolean existsByEmail(String email);
    
    void logUserActivity(User user, String action, String featureName, String details, String ipAddress, String userAgent);
}