package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;

public class NewOrderState implements OrderState {

    private final Order order;

    public NewOrderState(Order order) {
        this.order = order;
    }

    @Override
    public void confirmOrder() {
        // Transition to Processing state
        order.setState(new ProcessingOrderState(order));
    }

    @Override
    public void cancelOrder() {
        // Transition to Cancelled state
        order.setState(new CancelledOrderState(order));
    }

    @Override
    public void completeOrder() {
        throw new IllegalStateException("Cannot complete a new order directly. Must be confirmed first.");
    }

    @Override
    public String getStatus() {
        return "NEW";
    }
} 