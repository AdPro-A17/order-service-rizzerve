package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test individual concrete states
class NewOrderStateTest {

    private Order order;
    private OrderState state;

    @BeforeEach
    void setUp() {
        // Use a real Order object to test state transitions correctly
        order = new Order("T1");
        state = order.getState(); // Should be NewOrderState initially
        assertTrue(state instanceof NewOrderState);
    }

    @Test
    void testConfirmOrder_TransitionToProcessing() {
        state.confirmOrder();
        // Verify that the order's state was changed to ProcessingOrderState
        assertTrue(order.getState() instanceof ProcessingOrderState);
        assertEquals("PROCESSING", order.getStatus());
    }

    @Test
    void testCompleteOrder_InvalidTransition() {
        // A new order cannot be directly completed
        assertThrows(IllegalStateException.class, () -> {
            state.completeOrder();
        });
        // Verify state did not change
        assertTrue(order.getState() instanceof NewOrderState);
    }

    @Test
    void testGetStatusString() {
        assertEquals("NEW", state.getStatus());
    }
}


class ProcessingOrderStateTest {
    private Order order;
    private OrderState state;

    @BeforeEach
    void setUp() {
        order = new Order("T2");
        // Manually set the state to Processing for this test class
        order.setState(new ProcessingOrderState(order));
        state = order.getState();
        assertTrue(state instanceof ProcessingOrderState);
    }

    @Test
    void testConfirmOrder_InvalidTransition() {
         assertThrows(IllegalStateException.class, () -> state.confirmOrder());
         // Verify state remains Processing
         assertTrue(order.getState() instanceof ProcessingOrderState);
    }

    @Test
    void testCompleteOrder_TransitionToCompleted() {
        state.completeOrder();
        assertTrue(order.getState() instanceof CompletedOrderState);
        assertEquals("COMPLETED", order.getStatus());
    }

     @Test
    void testGetStatusString() {
        assertEquals("PROCESSING", state.getStatus());
    }
}

class CompletedOrderStateTest {
    private Order order;
    private OrderState state;

    @BeforeEach
    void setUp() {
        order = new Order("T3");
        order.setState(new CompletedOrderState(order));
        state = order.getState();
        assertTrue(state instanceof CompletedOrderState);
    }

    @Test
    void testConfirmOrder_InvalidTransition() {
        assertThrows(IllegalStateException.class, () -> state.confirmOrder());
        assertTrue(order.getState() instanceof CompletedOrderState);
    }

    @Test
    void testCompleteOrder_NoTransition() {
        // Should not throw error, maybe log? Test that state remains Completed.
        assertDoesNotThrow(() -> state.completeOrder());
        assertTrue(order.getState() instanceof CompletedOrderState);
    }

     @Test
    void testGetStatusString() {
        assertEquals("COMPLETED", state.getStatus());
    }
} 