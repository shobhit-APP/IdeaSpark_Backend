package com.ideaspark.shared.enums;

public enum SubscriptionType {
    FREE("Free Plan", 0),
    PREMIUM("Premium Plan", 299);
    
    private final String displayName;
    private final int price;
    
    SubscriptionType(String displayName, int price) {
        this.displayName = displayName;
        this.price = price;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getPrice() {
        return price;
    }
}