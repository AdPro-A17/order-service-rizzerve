package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;

public class CancelledOrderState implements OrderState {

    private final Order order;

    public CancelledOrderState(Order order) {
        this.order = order;
    }

    @Override
    public void confirmOrder() {
        throw new IllegalStateException("Cannot confirm a cancelled order.");
    }

    @Override
    public void cancelOrder() {
        // Already cancelled, do nothing or log warning
        System.out.println("Order is already cancelled.");
    }

    @Override
    public void completeOrder() {
        throw new IllegalStateException("Cannot complete a cancelled order.");
    }

    @Override
    public String getStatus() {
        return "CANCELLED";
    }
} 