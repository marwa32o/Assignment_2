package com.uet.legacy;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileOrderRepository implements OrderRepository {
    private final String logFilePath;

    public FileOrderRepository(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void save(String customerEmail, double payableAmount, String paymentStatus) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(LocalDateTime.now() + " | Order for: " + customerEmail + " | Payable: PKR " + payableAmount + " | Status: " + paymentStatus + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write order log: " + e.getMessage());
        }
    }
}