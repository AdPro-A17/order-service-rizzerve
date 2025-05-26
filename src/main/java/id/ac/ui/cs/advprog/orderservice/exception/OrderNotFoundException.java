package id.ac.ui.cs.advprog.orderservice.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderId) {
        super("Could not find order with ID: " + orderId);
    }
} 