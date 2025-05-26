package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.model.state.NewOrderState;
import id.ac.ui.cs.advprog.orderservice.model.state.ProcessingOrderState;
import id.ac.ui.cs.advprog.orderservice.model.state.CompletedOrderState;
import id.ac.ui.cs.advprog.orderservice.model.state.OrderState;

public class StateFactory {
    
    private StateFactory() {
        // Private constructor to hide the implicit public one
    }
    
    public static OrderState createState(String status, Order order) {
        return switch (status) {
            case "NEW" -> new NewOrderState(order);
            case "PROCESSING" -> new ProcessingOrderState(order);
            case "COMPLETED" -> new CompletedOrderState(order);
            default -> throw new IllegalArgumentException("Unknown order status: " + status);
        };
    }
}