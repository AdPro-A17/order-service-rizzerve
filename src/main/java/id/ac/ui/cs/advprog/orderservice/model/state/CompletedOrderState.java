package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompletedOrderState implements OrderState {

    public CompletedOrderState(Order order) {
        // Constructor parameter kept for consistency with interface
    }

    @Override
    public void confirmOrder() {
        throw new IllegalStateException("Cannot confirm a completed order.");
    }

    @Override
    public void completeOrder() {
        // Already completed, do nothing or log warning
        log.warn("Order is already completed.");
    }

    @Override
    public String getStatus() {
        return "COMPLETED";
    }
} 