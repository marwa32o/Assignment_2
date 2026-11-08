package com.uet.legacy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderValidator {
    public Optional<String> validate(Map<String, Object> orderData) {
        if (orderData == null || orderData.isEmpty()) {
            return Optional.of("Error: Order data is empty!");
        }
        String email = (String) orderData.get("customerEmail");
        if (email == null || !email.contains("@")) {
            return Optional.of("Error: Invalid customer email!");
        }
        List items = itemsOf(orderData);
        if (items == null || items.isEmpty()) {
            return Optional.of("Error: Cart has no items!");
        }
        for (Object obj : items) {
            Map item = (Map) obj;
            if (!item.containsKey("price") || !item.containsKey("quantity")) {
                return Optional.of("Error: Missing item price or quantity!");
            }
            double price = (double) item.get("price");
            int quantity = (int) item.get("quantity");
            if (price <= 0 || quantity <= 0) {
                return Optional.of("Error: Invalid price or quantity for item!");
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static List itemsOf(Map<String, Object> orderData) {
        Object itemsObj = orderData.get("items");
        if (itemsObj instanceof List) {
            return (List) itemsObj;
        }
        return null;
    }
}