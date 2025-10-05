package com.ideaspark.api.service.impl;

import com.ideaspark.api.repository.SubscriptionRepository;
import com.ideaspark.api.repository.UserRepository;
import com.ideaspark.api.service.interfaces.SubscriptionService;
import com.ideaspark.api.service.interfaces.UserService;
import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.entity.Subscription;
import com.ideaspark.shared.entity.User;
import com.ideaspark.shared.enums.SubscriptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public ResponseDTO<Object> getAvailablePlans() {
        try {
            List<Map<String, Object>> plans = Arrays.asList(
                createPlan(1, "Free Plan", 0, "UNLIMITED", 
                    Arrays.asList("AI Chat (Limited)", "Basic Text Tools", "5 Daily Requests")),
                createPlan(2, "Premium Plan", 299, "MONTHLY",
                    Arrays.asList("Unlimited AI Chat", "News Fake Detector", "All Text Tools", 
                                "Image Generator", "Code Assistant", "Voice Tools", "Data Export", "Priority Support"))
            );

            return ResponseDTO.success(plans);

        } catch (Exception e) {
            log.error("Error getting subscription plans: {}", e.getMessage());
            return ResponseDTO.error("PLANS_FETCH_FAILED", "Failed to fetch subscription plans");
        }
    }

    @Override
    public ResponseDTO<Map<String, Object>> subscribe(String userEmail, int planId, Map<String, Object> paymentDetails) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Cancel existing active subscription
            Optional<Subscription> existingSubscription = subscriptionRepository.findByUserAndStatus(user, "active");
            existingSubscription.ifPresent(sub -> {
                sub.setStatus("cancelled");
                sub.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.save(sub);
            });

            // Create new subscription
            SubscriptionType type = planId == 2 ? SubscriptionType.PREMIUM : SubscriptionType.FREE;
            String subscriptionId = "sub_" + System.currentTimeMillis();
            
            Subscription subscription = Subscription.builder()
                    .user(user)
                    .subscriptionId(subscriptionId)
                    .type(type)
                    .status("active")
                    .startsAt(LocalDateTime.now())
                    .expiresAt(planId == 2 ? LocalDateTime.now().plusMonths(1) : null)
                    .autoRenew(planId == 2)
                    .amount(planId == 2 ? 299 : 0)
                    .currency("USD")
                    .createdAt(LocalDateTime.now())
                    .build();

            subscriptionRepository.save(subscription);

            // Update user premium status
            user.setIsPremium(planId == 2);
            user.setPremiumExpiresAt(subscription.getExpiresAt());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            // Log activity
            userService.logUserActivity(user, "SUBSCRIPTION", "subscription", 
                "Subscribed to " + type.getDisplayName(), null, null);

            Map<String, Object> response = new HashMap<>();
            response.put("subscriptionId", subscriptionId);
            response.put("planName", type.getDisplayName());
            response.put("startDate", subscription.getStartsAt());
            response.put("endDate", subscription.getExpiresAt());
            response.put("amount", subscription.getAmount());
            response.put("status", "ACTIVE");

            return ResponseDTO.success("Subscription activated successfully", response);

        } catch (Exception e) {
            log.error("Error subscribing user: {}", e.getMessage());
            return ResponseDTO.error("SUBSCRIPTION_FAILED", "Failed to activate subscription");
        }
    }

    @Override
    public ResponseDTO<Map<String, Object>> getSubscriptionStatus(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Subscription> subscription = subscriptionRepository.findByUserAndStatus(user, "active");
            
            if (subscription.isPresent()) {
                Subscription sub = subscription.get();
                Map<String, Object> response = new HashMap<>();
                response.put("subscriptionId", sub.getSubscriptionId());
                response.put("planName", sub.getType().getDisplayName());
                response.put("status", "ACTIVE");
                response.put("startDate", sub.getStartsAt());
                response.put("endDate", sub.getExpiresAt());
                
                if (sub.getExpiresAt() != null) {
                    long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), sub.getExpiresAt());
                    response.put("daysRemaining", Math.max(0, daysRemaining));
                }
                
                response.put("autoRenewal", sub.getAutoRenew());
                response.put("features", getFeaturesByType(sub.getType()));

                return ResponseDTO.success(response);
            } else {
                // Return free plan info
                Map<String, Object> response = new HashMap<>();
                response.put("planName", "Free Plan");
                response.put("status", "ACTIVE");
                response.put("features", getFeaturesByType(SubscriptionType.FREE));
                
                return ResponseDTO.success(response);
            }

        } catch (Exception e) {
            log.error("Error getting subscription status: {}", e.getMessage());
            return ResponseDTO.error("STATUS_FETCH_FAILED", "Failed to get subscription status");
        }
    }

    @Override
    public ResponseDTO<Map<String, Object>> cancelSubscription(String userEmail, String reason, boolean cancelImmediately) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Subscription> subscription = subscriptionRepository.findByUserAndStatus(user, "active");
            
            if (subscription.isPresent()) {
                Subscription sub = subscription.get();
                sub.setStatus("cancelled");
                sub.setAutoRenew(false);
                sub.setUpdatedAt(LocalDateTime.now());
                
                LocalDateTime endDate = sub.getExpiresAt();
                if (cancelImmediately) {
                    endDate = LocalDateTime.now();
                    user.setIsPremium(false);
                    user.setPremiumExpiresAt(null);
                    userRepository.save(user);
                }
                
                subscriptionRepository.save(sub);

                // Log activity
                userService.logUserActivity(user, "SUBSCRIPTION_CANCEL", "subscription", 
                    "Subscription cancelled: " + reason, null, null);

                Map<String, Object> response = new HashMap<>();
                response.put("subscriptionId", sub.getSubscriptionId());
                response.put("status", "CANCELLED");
                response.put("endDate", endDate);
                response.put("refundAmount", 0);

                return ResponseDTO.success("Subscription cancelled successfully", response);
            } else {
                return ResponseDTO.error("NO_ACTIVE_SUBSCRIPTION", "No active subscription found");
            }

        } catch (Exception e) {
            log.error("Error cancelling subscription: {}", e.getMessage());
            return ResponseDTO.error("CANCEL_FAILED", "Failed to cancel subscription");
        }
    }

    @Override
    public ResponseDTO<Object> getSubscriptionHistory(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Subscription> subscriptions = subscriptionRepository.findByUserOrderByCreatedAtDesc(user);
            
            List<Map<String, Object>> history = subscriptions.stream()
                    .map(sub -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("subscriptionId", sub.getSubscriptionId());
                        item.put("planName", sub.getType().getDisplayName());
                        item.put("startDate", sub.getStartsAt());
                        item.put("endDate", sub.getExpiresAt());
                        item.put("amount", sub.getAmount());
                        item.put("status", sub.getStatus().toUpperCase());
                        item.put("paymentMethod", "CARD"); // Default for now
                        return item;
                    })
                    .toList();

            return ResponseDTO.success(history);

        } catch (Exception e) {
            log.error("Error getting subscription history: {}", e.getMessage());
            return ResponseDTO.error("HISTORY_FETCH_FAILED", "Failed to get subscription history");
        }
    }

    @Override
    public ResponseDTO<Map<String, Object>> upgradeSubscription(String userEmail, int newPlanId, boolean prorated) {
        try {
            // For now, treat upgrade as a new subscription
            Map<String, Object> paymentDetails = new HashMap<>();
            ResponseDTO<Map<String, Object>> result = subscribe(userEmail, newPlanId, paymentDetails);
            
            if (result.isSuccess()) {
                Map<String, Object> response = new HashMap<>();
                response.put("newPlanName", newPlanId == 2 ? "Premium Plan" : "Free Plan");
                response.put("proratedAmount", prorated ? 150 : 299);
                response.put("newEndDate", LocalDateTime.now().plusMonths(1));
                
                return ResponseDTO.success("Subscription upgraded successfully", response);
            }
            
            return result;

        } catch (Exception e) {
            log.error("Error upgrading subscription: {}", e.getMessage());
            return ResponseDTO.error("UPGRADE_FAILED", "Failed to upgrade subscription");
        }
    }

    @Override
    public boolean hasValidSubscription(String userEmail, SubscriptionType requiredType) {
        try {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) return false;

            if (requiredType == SubscriptionType.FREE) return true;

            Optional<Subscription> subscription = subscriptionRepository.findByUserAndStatus(user, "active");
            if (subscription.isEmpty()) return false;

            Subscription sub = subscription.get();
            return sub.getType() == requiredType && 
                   (sub.getExpiresAt() == null || sub.getExpiresAt().isAfter(LocalDateTime.now()));

        } catch (Exception e) {
            log.error("Error checking subscription validity: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void processExpiredSubscriptions() {
        try {
            List<Subscription> expiredSubscriptions = subscriptionRepository
                    .findExpiredActiveSubscriptions(LocalDateTime.now());

            for (Subscription subscription : expiredSubscriptions) {
                subscription.setStatus("expired");
                subscription.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.save(subscription);

                // Update user premium status
                User user = subscription.getUser();
                user.setIsPremium(false);
                user.setPremiumExpiresAt(null);
                userRepository.save(user);

                log.info("Processed expired subscription: {}", subscription.getSubscriptionId());
            }

        } catch (Exception e) {
            log.error("Error processing expired subscriptions: {}", e.getMessage());
        }
    }

    @Override
    public void sendExpirationNotifications() {
        try {
            LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
            LocalDateTime now = LocalDateTime.now();
            
            List<Subscription> expiringSubscriptions = subscriptionRepository
                    .findExpiringSubscriptions(now, threeDaysFromNow);

            for (Subscription subscription : expiringSubscriptions) {
                // In a real implementation, send email/push notification
                log.info("Subscription expiring soon: {}", subscription.getSubscriptionId());
            }

        } catch (Exception e) {
            log.error("Error sending expiration notifications: {}", e.getMessage());
        }
    }

    private Map<String, Object> createPlan(int id, String name, int price, String duration, List<String> features) {
        Map<String, Object> plan = new HashMap<>();
        plan.put("id", id);
        plan.put("name", name);
        plan.put("price", price);
        plan.put("duration", duration);
        plan.put("features", features);
        return plan;
    }

    private List<String> getFeaturesByType(SubscriptionType type) {
        if (type == SubscriptionType.PREMIUM) {
            return Arrays.asList("Unlimited AI Chat", "News Fake Detector", "Data Export");
        } else {
            return Arrays.asList("AI Chat (Limited)", "Basic Text Tools", "5 Daily Requests");
        }
    }
}