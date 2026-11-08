package com.uet.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrderBillingEngineTest {

    private OrderBillingEngine engine;
    private Path logFile;
    private ByteArrayOutputStream console;
    private PrintStream originalOut;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        logFile = tempDir.resolve("audit.log");
        engine = new OrderBillingEngine();
        engine.setLogFilePath(logFile.toString());
        console = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(console, true));
    }

    private Map<String, Object> baseOrder() {
        Map<String, Object> order = new HashMap<>();
        order.put("customerEmail", "aisha@example.com");
        List items = new ArrayList<>();
        items.add(item(1000.0, 2));
        order.put("items", items);
        order.put("paymentMethod", "CREDIT_CARD");
        order.put("cardNumber", "1234567890123456");
        return order;
    }

    private Map<String, Object> item(double price, int quantity) {
        Map<String, Object> itm = new HashMap<>();
        itm.put("price", price);
        itm.put("quantity", quantity);
        return itm;
    }

    private Map<String, Object> orderWithItem(double price, int quantity) {
        Map<String, Object> order = baseOrder();
        List items = new ArrayList<>();
        items.add(item(price, quantity));
        order.put("items", items);
        return order;
    }

    private String output() {
        return console.toString();
    }

    @Test
    void standardOrderSucceeds() throws IOException {
        assertTrue(engine.processOrder(baseOrder()));
        String logContent = Files.readString(logFile);
        assertTrue(logContent.contains("aisha@example.com"));
        assertTrue(logContent.contains("PAID"));
    }

    @Test
    void vipGetsFifteenPercentAboveFiveThousand() {
        Map<String, Object> order = orderWithItem(3000.0, 2);
        order.put("customerType", "VIP");
        assertTrue(engine.processOrder(order));
    }

    @Test
    void creditCardWithFifteenDigitsIsDeclined() {
        Map<String, Object> order = baseOrder();
        order.put("cardNumber", "123456789012345");
        assertFalse(engine.processOrder(order));
        assertTrue(output().contains("Card declined: Must be 16 digits."));
    }

    @Test
    void cashOnDeliveryAboveLimitIsRejected() {
        Map<String, Object> order = orderWithItem(15000.0, 2);
        order.put("paymentMethod", "CASH_ON_DELIVERY");
        assertFalse(engine.processOrder(order));
        assertTrue(output().contains("Cash on delivery not allowed for orders above PKR 25,000."));
    }

    @Test
    void negativePriceIsRejected() {
        assertFalse(engine.processOrder(orderWithItem(-50.0, 1)));
        assertTrue(output().contains("Error: Invalid price or quantity for item!"));
    }

    @Test
    void smsNotificationUsesGivenPhone() {
        Map<String, Object> order = baseOrder();
        order.put("notifyChannel", "SMS");
        order.put("phone", "0311-1234567");
        engine.processOrder(order);
        assertTrue(output().contains("[SMS DISPATCHED] To: 0311-1234567 | Order confirmed!"));
        assertFalse(output().contains("[EMAIL DISPATCHED]"));
    }
}