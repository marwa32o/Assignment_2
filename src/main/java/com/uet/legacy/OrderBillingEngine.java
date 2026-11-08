package com.uet.legacy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Coordinates the order workflow only. All real work is delegated:
 * validation, discounts, tax, payment, persistence and notification.
 */
public class OrderBillingEngine {
    private static final String DEFAULT_LOG_FILE = "order_audit.log";
    private final OrderValidator validator;
    private final DiscountCalculator discountCalculator;
    private final TaxCalculator taxCalculator;
    private final Map<String, PaymentStrategy> paymentStrategies = new HashMap<>();
    private final Map<String, NotificationService> notifiers = new HashMap<>();
    private OrderRepository repository;
    private String logFilePath = DEFAULT_LOG_FILE;

    public OrderBillingEngine() {
        this(new FileOrderRepository(DEFAULT_LOG_FILE));
    }

    public OrderBillingEngine(OrderRepository repository) {
        this(new OrderValidator(), new DiscountCalculator(), new TaxCalculator(), repository);
    }

    public OrderBillingEngine(OrderValidator validator, DiscountCalculator discountCalculator,
                              TaxCalculator taxCalculator, OrderRepository repository) {
        this.validator = validator;
        this.discountCalculator = discountCalculator;
        this.taxCalculator = taxCalculator;
        this.repository = repository;
        registerPayment(new CreditCardPayment());
        registerPayment(new EasyPaisaPayment());
        registerPayment(new CashOnDeliveryPayment());
        registerNotifier(new EmailNotificationService());
        registerNotifier(new SmsNotificationService());
    }

    public final void registerPayment(PaymentStrategy strategy) {
        paymentStrategies.put(strategy.methodName(), strategy);
    }

    public final void registerNotifier(NotificationService notifier) {
        notifiers.put(notifier.channel(), notifier);
    }

    public boolean processOrder(Map<String, Object> orderData) {
        Optional<String> error = validator.validate(orderData);
        if (error.isPresent()) {
            System.out.println(error.get());
            return false;
        }
        List items = OrderValidator.itemsOf(orderData);
        double rawTotal = calculateRawTotal(items);
        String customerType = (String) orderData.getOrDefault("customerType", "REGULAR");
        double netAmount = rawTotal - discountCalculator.calculate(customerType, rawTotal, items.size());
        String country = (String) orderData.getOrDefault("country", "PK");
        double payableAmount = netAmount + taxCalculator.calculateTax(country, netAmount);
        String paymentMethod = (String) orderData.getOrDefault("paymentMethod", "CASH");
        PaymentStrategy strategy = paymentStrategies.get(paymentMethod);
        if (strategy == null) {
            System.out.println("Error: Unsupported payment method: " + paymentMethod);
            return false;
        }
        PaymentResult result = strategy.pay(payableAmount, orderData);
        if (!result.success()) {
            return false;
        }
        String email = (String) orderData.get("customerEmail");
        repository.save(email, payableAmount, result.status());
        String channel = (String) orderData.getOrDefault("notifyChannel", "EMAIL");
        NotificationService notifier = notifiers.get(channel);
        if (notifier != null) {
            notifier.send(email, orderData, payableAmount);
        }
        return true;
    }

    private double calculateRawTotal(List items) {
        double total = 0.0;
        if (items != null) {
            for (Object obj : items) {
                Map item = (Map) obj;
                total += (double) item.get("price") * (int) item.get("quantity");
            }
        }
        return total;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
        this.repository = new FileOrderRepository(logFilePath);
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}