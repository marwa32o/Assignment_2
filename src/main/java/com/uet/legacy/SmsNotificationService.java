package com.uet.legacy;

import java.util.Map;

public class SmsNotificationService implements NotificationService {
    @Override
    public String channel() {
        return "SMS";
    }
    @Override
    public void send(String customerEmail, Map<String, Object> orderData, double payableAmount) {
        String phone = (String) orderData.getOrDefault("phone", "0300-0000000");
        System.out.println("[SMS DISPATCHED] To: " + phone + " | Order confirmed! Paid: PKR " + String.format("%.2f", payableAmount));
    }
}