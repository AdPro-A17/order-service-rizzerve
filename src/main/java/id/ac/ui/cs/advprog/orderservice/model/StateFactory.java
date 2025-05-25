package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.model.state.NewOrderState;
import id.ac.ui.cs.advprog.orderservice.model.state.OrderState;

public class StateFactory {
    public static OrderState createState(String status, Order order) {
        return switch (status) {
            case "NEW" -> new NewOrderState(order);
            case "PROCESSING" -> new id.ac.ui.cs.advprog.orderservice.model.state.ProcessingOrderState(order);
            case "COMPLETED" -> new id.ac.ui.cs.advprog.orderservice.model.state.CompletedOrderState(order);
            default -> throw new IllegalArgumentException("Unknown order status: " + status);
        };
    }
}