package com.uet.legacy;

import java.util.Map;

/** One implementation per notification channel (EMAIL, SMS, ...). */
public interface NotificationService {
    String channel();
    void send(String customerEmail, Map<String, Object> orderData, double payableAmount);
}