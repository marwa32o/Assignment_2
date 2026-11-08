package com.uet.legacy;

import java.util.Map;

/** Strategy interface: one implementation per payment method. */
public interface PaymentStrategy {
    String methodName();
    PaymentResult pay(double amount, Map<String, Object> orderData);
}