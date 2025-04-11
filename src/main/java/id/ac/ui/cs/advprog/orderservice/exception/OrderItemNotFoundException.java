package id.ac.ui.cs.advprog.orderservice.exception;

import java.util.UUID;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(UUID orderItemId, UUID orderId) {
        super("Could not find order item with ID: " + orderItemId + " in order with ID: " + orderId);
    }
} 