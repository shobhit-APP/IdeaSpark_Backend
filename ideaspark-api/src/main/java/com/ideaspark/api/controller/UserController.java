package com.ideaspark.api.controller;

import com.ideaspark.api.service.interfaces.AuthService;
import com.ideaspark.api.service.interfaces.UserService;
import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.dto.UserDTO;
import com.ideaspark.shared.enums.UserRole;
import com.ideaspark.shared.enums.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

        /**
         * Get user profile
         * @return User profile details
         */
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Get user profile",
            description = "Returns the profile of the authenticated user"
        )
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid token")
        })
    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserProfile(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<UserDTO> response = userService.getUserProfile(email);
        return ResponseEntity.ok(response);
    }

        /**
         * Update user profile
         * @param userDTO Updated user data
         * @return Updated user profile
         */
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Update user profile",
            description = "Updates the profile of the authenticated user"
        )
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid token or data")
        })
    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO<UserDTO>> updateUserProfile(
            @RequestBody UserDTO userDTO,
            HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<UserDTO> response = userService.updateUserProfile(email, userDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Get all users (admin)",
            description = "Returns a paginated list of all users. Admin only."
        )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        UserRole userRole = role != null ? UserRole.valueOf(role.toUpperCase()) : null;
        UserStatus userStatus = status != null ? UserStatus.valueOf(status.toUpperCase()) : null;
        
        ResponseDTO<Map<String, Object>> response = userService.getAllUsers(page, size, userRole, userStatus);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/status")
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Update user status (admin)",
            description = "Updates the status of a user. Admin only."
        )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<String>> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        
        String statusStr = request.get("status");
        UserStatus status = UserStatus.valueOf(statusStr.toUpperCase());
        
        ResponseDTO<String> response = userService.updateUserStatus(userId, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Delete user (admin)",
            description = "Deletes a user by ID. Admin only."
        )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<String>> deleteUser(@PathVariable String userId) {
        ResponseDTO<String> response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Get user statistics (admin)",
            description = "Returns statistics about users. Admin only."
        )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getUserStatistics() {
        ResponseDTO<Map<String, Object>> response = userService.getUserStatistics();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Change password",
            description = "Allows a user to change their password."
        )
    public ResponseEntity<ResponseDTO<String>> changePassword(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        String token = extractTokenFromRequest(httpRequest);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        ResponseDTO<String> response = authService.changePassword(email, currentPassword, newPassword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activity")
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Get user activity",
            description = "Returns a paginated list of user activity logs."
        )
    public ResponseEntity<ResponseDTO<Page<Map<String, Object>>>> getUserActivity(
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request) {
        
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<Page<Map<String, Object>>> response = userService.getUserActivity(email, limit);
        return ResponseEntity.ok(response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}