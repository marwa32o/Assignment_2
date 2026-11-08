package com.uet.legacy;

/** Abstraction for saving a processed order (file, database, ...). */
public interface OrderRepository {
    void save(String customerEmail, double payableAmount, String paymentStatus);
}