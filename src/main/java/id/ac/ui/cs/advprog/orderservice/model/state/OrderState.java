package id.ac.ui.cs.advprog.orderservice.model.state;

/**
 * Interface defining the state-specific behavior for an Order.
 */
public interface OrderState {

    
    void confirmOrder();

    
    void completeOrder();

    /**
     * Returns the status string representation of the current state.
     * @return String status (e.g., "NEW", "PROCESSING", "COMPLETED", "CANCELLED")
     */
    String getStatus();
} 