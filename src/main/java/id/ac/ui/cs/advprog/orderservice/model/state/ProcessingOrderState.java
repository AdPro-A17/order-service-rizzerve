package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;

public class ProcessingOrderState implements OrderState {

    private final Order order;

    public ProcessingOrderState(Order order) {
        this.order = order;
    }

    @Override
    public void confirmOrder() {
        throw new IllegalStateException("Order is already being processed.");
    }

    @Override
    public void cancelOrder() {
        // Transition to Cancelled state
        order.setState(new CancelledOrderState(order));
        // Potentially add logic here to notify kitchen, release stock etc.
    }

    @Override
    public void completeOrder() {
        // Transition to Completed state
        order.setState(new CompletedOrderState(order));
        // Potentially add logic here to notify billing/payment service etc.
    }

    @Override
    public String getStatus() {
        return "PROCESSING";
    }
} 