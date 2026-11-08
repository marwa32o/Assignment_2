package com.uet.legacy;

public record PaymentResult(boolean success, String status) {
    public static PaymentResult paid(String status) {
        return new PaymentResult(true, status);
    }
    public static PaymentResult failed() {
        return new PaymentResult(false, "FAILED");
    }
}