package com.uet.legacy;

import java.util.Map;

public class CashOnDeliveryPayment implements PaymentStrategy {
    private static final double MAX_COD_AMOUNT = 25000.0;
    @Override
    public String methodName() {
        return "CASH_ON_DELIVERY";
    }
    @Override
    public PaymentResult pay(double amount, Map<String, Object> orderData) {
        if (amount > MAX_COD_AMOUNT) {
            System.out.println("Cash on delivery not allowed for orders above PKR 25,000.");
            return PaymentResult.failed();
        }
        return PaymentResult.paid("PENDING_CASH");
    }
}