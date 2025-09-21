package com.example.ecommerce.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderNumberGenerator {

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicLong counter = new AtomicLong(1);

    public String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DATE_FORMAT);
        long sequence = counter.getAndIncrement();
        return String.format("%s-%s-%06d", PREFIX, datePart, sequence);
    }

    public String generateOrderNumber(LocalDateTime dateTime) {
        String datePart = dateTime.format(DATE_FORMAT);
        long sequence = counter.getAndIncrement();
        return String.format("%s-%s-%06d", PREFIX, datePart, sequence);
    }

    public boolean isValidOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.isEmpty()) {
            return false;
        }

        String pattern = "^" + PREFIX + "-\\d{8}-\\d{6}$";
        return orderNumber.matches(pattern);
    }

    public String extractDateFromOrderNumber(String orderNumber) {
        if (!isValidOrderNumber(orderNumber)) {
            throw new IllegalArgumentException("Invalid order number format");
        }

        String[] parts = orderNumber.split("-");
        return parts[1]; // Returns the date part (yyyyMMdd)
    }

    public long extractSequenceFromOrderNumber(String orderNumber) {
        if (!isValidOrderNumber(orderNumber)) {
            throw new IllegalArgumentException("Invalid order number format");
        }

        String[] parts = orderNumber.split("-");
        return Long.parseLong(parts[2]); // Returns the sequence number
    }
}