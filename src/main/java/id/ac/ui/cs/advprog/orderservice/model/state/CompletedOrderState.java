package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;

public class CompletedOrderState implements OrderState {

    private final Order order;

    public CompletedOrderState(Order order) {
        this.order = order;
    }

    @Override
    public void confirmOrder() {
        throw new IllegalStateException("Cannot confirm a completed order.");
    }

    @Override
    public void cancelOrder() {
        throw new IllegalStateException("Cannot cancel a completed order.");
    }

    @Override
    public void completeOrder() {
        // Already completed, do nothing or log warning
        System.out.println("Order is already completed.");
    }

    @Override
    public String getStatus() {
        return "COMPLETED";
    }
} 