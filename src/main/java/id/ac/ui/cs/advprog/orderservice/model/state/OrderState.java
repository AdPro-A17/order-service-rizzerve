package id.ac.ui.cs.advprog.orderservice.model.state;

/**
 * Interface defining the state-specific behavior for an Order.
 */
public interface OrderState {

    /**
     * Action to confirm the order.
     * May transition the order to the next state (e.g., PROCESSING).
     * Throws IllegalStateException if the action is not allowed in the current state.
     */
    void confirmOrder();

    /**
     * Action to mark the order as completed.
     * May transition the order to the COMPLETED state.
     * Throws IllegalStateException if the action is not allowed in the current state.
     */
    void completeOrder();

    /**
     * Returns the status string representation of the current state.
     * @return String status (e.g., "NEW", "PROCESSING", "COMPLETED", "CANCELLED")
     */
    String getStatus();
} 