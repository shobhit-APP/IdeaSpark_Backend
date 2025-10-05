package com.ideaspark.api.controller;

import com.ideaspark.api.service.interfaces.AuthService;
import com.ideaspark.api.service.interfaces.SubscriptionService;
import com.ideaspark.shared.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final AuthService authService;

        /**
         * Get available subscription plans
         */
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Get available subscription plans",
            description = "Returns a list of available subscription plans"
        )
    @GetMapping("/plans")
    public ResponseEntity<ResponseDTO<Object>> getAvailablePlans() {
        ResponseDTO<Object> response = subscriptionService.getAvailablePlans();
        return ResponseEntity.ok(response);
    }

        /**
         * Subscribe to a plan
         * @param request Subscription request data
         * @return Subscription result
         */
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Subscribe to a plan",
            description = "Subscribes the authenticated user to a selected plan"
        )
    @PostMapping("/subscribe")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> subscribe(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        String token = extractTokenFromRequest(httpRequest);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        int planId = (Integer) request.get("planId");
        Map<String, Object> paymentDetails = (Map<String, Object>) request.get("paymentDetails");

        ResponseDTO<Map<String, Object>> response = subscriptionService.subscribe(email, planId, paymentDetails);
        return ResponseEntity.ok(response);
    }

        /**
         * Get subscription status
         * @return Current subscription status
         */
        @io.swagger.v3.oas.annotations.Operation(
            summary = "Get subscription status",
            description = "Returns the current subscription status of the authenticated user"
        )
    @GetMapping("/status")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getSubscriptionStatus(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<Map<String, Object>> response = subscriptionService.getSubscriptionStatus(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> cancelSubscription(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        String token = extractTokenFromRequest(httpRequest);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        String reason = (String) request.getOrDefault("reason", "User requested cancellation");
        boolean cancelImmediately = (Boolean) request.getOrDefault("cancelImmediately", false);

        ResponseDTO<Map<String, Object>> response = subscriptionService.cancelSubscription(email, reason, cancelImmediately);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseDTO<Object>> getSubscriptionHistory(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        ResponseDTO<Object> response = subscriptionService.getSubscriptionHistory(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/upgrade")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> upgradeSubscription(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        String token = extractTokenFromRequest(httpRequest);
        if (token == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.error("INVALID_TOKEN", "Token required"));
        }

        String email = authService.extractEmailFromToken(token);
        int newPlanId = (Integer) request.get("newPlanId");
        boolean prorated = (Boolean) request.getOrDefault("prorated", true);

        ResponseDTO<Map<String, Object>> response = subscriptionService.upgradeSubscription(email, newPlanId, prorated);
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