package com.uet.legacy;

import java.util.Map;

public class CreditCardPayment implements PaymentStrategy {
    private static final int CARD_LENGTH = 16;
    @Override
    public String methodName() {
        return "CREDIT_CARD";
    }
    @Override
    public PaymentResult pay(double amount, Map<String, Object> orderData) {
        String cardNumber = (String) orderData.getOrDefault("cardNumber", "");
        if (cardNumber.length() == CARD_LENGTH && cardNumber.matches("\\d+")) {
            System.out.println("Processing credit card payment of PKR " + amount);
            return PaymentResult.paid("PAID");
        }
        System.out.println("Card declined: Must be 16 digits.");
        return PaymentResult.failed();
    }
}