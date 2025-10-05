package com.ideaspark.api.service.impl;

import com.ideaspark.api.repository.UserActivityRepository;
import com.ideaspark.api.repository.UserRepository;
import com.ideaspark.api.service.interfaces.UserService;
import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.dto.UserDTO;
import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.entity.UserActivity;
import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;

    @Override
    public ResponseDTO<UserDTO> getUserProfile(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDTO userDTO = convertToUserDTO(user);
            return ResponseDTO.success(userDTO);

        } catch (Exception e) {
            log.error("Error getting user profile: {}", e.getMessage());
            return ResponseDTO.error("USER_NOT_FOUND", "User profile not found");
        }
    }

    @Override
    public ResponseDTO<UserDTO> updateUserProfile(String email, UserDTO userDTO) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update allowed fields
            if (userDTO.getFullName() != null) {
                user.setFullName(userDTO.getFullName());
            }
            if (userDTO.getPhone() != null) {
                user.setPhone(userDTO.getPhone());
            }
            if (userDTO.getCountry() != null) {
                user.setCountry(userDTO.getCountry());
            }
            if (userDTO.getLanguage() != null) {
                user.setLanguage(userDTO.getLanguage());
            }
            if (userDTO.getProfileImageUrl() != null) {
                user.setProfileImageUrl(userDTO.getProfileImageUrl());
            }

            user.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            // Log activity
            logUserActivity(user, "PROFILE_UPDATE", "profile", "Profile updated", null, null);

            return ResponseDTO.success("Profile updated successfully", convertToUserDTO(savedUser));

        } catch (Exception e) {
            log.error("Error updating user profile: {}", e.getMessage());
            return ResponseDTO.error("UPDATE_FAILED", "Failed to update profile");
        }
    }

    @Override
    public ResponseDTO<Map<String, Object>> getAllUsers(int page, int size, UserRole role, UserStatus status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage;

            if (role != null && status != null) {
                userPage = userRepository.findByRoleAndStatus(role, status, pageable);
            } else if (role != null) {
                userPage = userRepository.findByRole(role, pageable);
            } else if (status != null) {
                userPage = userRepository.findByStatus(status, pageable);
            } else {
                userPage = userRepository.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent().stream()
                    .map(this::convertToUserDTO)
                    .collect(Collectors.toList()));
            response.put("totalUsers", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("currentPage", userPage.getNumber());

            return ResponseDTO.success(response);

        } catch (Exception e) {
            log.error("Error getting all users: {}", e.getMessage());
            return ResponseDTO.error("FETCH_FAILED", "Failed to fetch users");
        }
    }

    @Override
    public ResponseDTO<String> updateUserStatus(String userId, UserStatus status) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setStatus(status);
            user.setIsActive(status == UserStatus.ACTIVE);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logUserActivity(user, "STATUS_UPDATE", "admin", "Status updated to " + status, null, null);

            return ResponseDTO.success("User status updated successfully");

        } catch (Exception e) {
            log.error("Error updating user status: {}", e.getMessage());
            return ResponseDTO.error("UPDATE_FAILED", "Failed to update user status");
        }
    }

    @Override
    public ResponseDTO<String> deleteUser(String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Soft delete - mark as inactive
            user.setStatus(UserStatus.INACTIVE);
            user.setIsActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logUserActivity(user, "USER_DELETE", "admin", "User deleted by admin", null, null);

            return ResponseDTO.success("User deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseDTO.error("DELETE_FAILED", "Failed to delete user");
        }
    }

    @Override
    public ResponseDTO<Map<String, Object>> getUserStatistics() {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthStart = today.withDayOfMonth(1);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userRepository.count());
            stats.put("activeUsers", userRepository.countByStatus(UserStatus.ACTIVE));
            stats.put("premiumUsers", userRepository.countByIsPremium(true));
            stats.put("freeUsers", userRepository.countByIsPremium(false));
            stats.put("newUsersToday", userRepository.countByCreatedAtAfter(today));
            stats.put("newUsersThisMonth", userRepository.countByCreatedAtAfter(monthStart));

            return ResponseDTO.success(stats);

        } catch (Exception e) {
            log.error("Error getting user statistics: {}", e.getMessage());
            return ResponseDTO.error("STATS_FAILED", "Failed to get user statistics");
        }
    }

    @Override
    public ResponseDTO<Page<Map<String, Object>>> getUserActivity(String email, int limit) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(0, limit);
            Page<UserActivity> activities = userActivityRepository.findByUserOrderByCreatedAtDesc(user, pageable);

            Page<Map<String, Object>> activityData = activities.map(activity -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", activity.getId());
                data.put("action", activity.getAction());
                data.put("timestamp", activity.getCreatedAt());
                data.put("ipAddress", activity.getIpAddress());
                data.put("deviceInfo", activity.getDeviceInfo());
                data.put("details", activity.getDetails());
                return data;
            });

            return ResponseDTO.success(activityData);

        } catch (Exception e) {
            log.error("Error getting user activity: {}", e.getMessage());
            return ResponseDTO.error("ACTIVITY_FAILED", "Failed to get user activity");
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void logUserActivity(User user, String action, String featureName, String details, String ipAddress, String userAgent) {
        try {
            UserActivity activity = UserActivity.builder()
                    .user(user)
                    .action(action)
                    .featureName(featureName)
                    .details(details)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .createdAt(LocalDateTime.now())
                    .build();

            userActivityRepository.save(activity);
        } catch (Exception e) {
            log.error("Error logging user activity: {}", e.getMessage());
        }
    }

    private UserDTO convertToUserDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileImageUrl(user.getProfileImageUrl())
                .phone(user.getPhone())
                .country(user.getCountry())
                .language(user.getLanguage())
                .role(user.getRole())
                .status(user.getStatus())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .isPremium(user.getIsPremium())
                .subscriptionType(user.getIsPremium() ? 
                    com.ideaspark.shared.enums.SubscriptionType.PREMIUM : 
                    com.ideaspark.shared.enums.SubscriptionType.FREE)
                .premiumExpiresAt(user.getPremiumExpiresAt())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}