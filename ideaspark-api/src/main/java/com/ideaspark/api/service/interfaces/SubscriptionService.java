package com.ideaspark.api.service.interfaces;

import com.ideaspark.shared.dto.ResponseDTO;
import com.ideaspark.shared.enums.SubscriptionType;

import java.util.Map;

public interface SubscriptionService {
    
    ResponseDTO<Object> getAvailablePlans();
    
    ResponseDTO<Map<String, Object>> subscribe(String userEmail, int planId, Map<String, Object> paymentDetails);
    
    ResponseDTO<Map<String, Object>> getSubscriptionStatus(String userEmail);
    
    ResponseDTO<Map<String, Object>> cancelSubscription(String userEmail, String reason, boolean cancelImmediately);
    
    ResponseDTO<Object> getSubscriptionHistory(String userEmail);
    
    ResponseDTO<Map<String, Object>> upgradeSubscription(String userEmail, int newPlanId, boolean prorated);
    
    boolean hasValidSubscription(String userEmail, SubscriptionType requiredType);
    
    void processExpiredSubscriptions();
    
    void sendExpirationNotifications();
}