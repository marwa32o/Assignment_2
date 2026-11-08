package com.uet.legacy;

import java.util.Map;

public class EmailNotificationService implements NotificationService {
    @Override
    public String channel() {
        return "EMAIL";
    }
    @Override
    public void send(String customerEmail, Map<String, Object> orderData, double payableAmount) {
        System.out.println("[EMAIL DISPATCHED] To: " + customerEmail + " | Your order of PKR " + String.format("%.2f", payableAmount) + " is confirmed!");
    }
}