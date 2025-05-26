package id.ac.ui.cs.advprog.orderservice.model.state;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewOrderStateTest {
    private Order order;
    private OrderState state;

    @BeforeEach
    void setUp() {
        order = new Order("T1");
        state = order.getState();
        assertTrue(state instanceof NewOrderState);
    }

    @Test
    void testConfirmOrder_TransitionToProcessing() {
        state.confirmOrder();
        assertTrue(order.getState() instanceof ProcessingOrderState);
        assertEquals("PROCESSING", order.getStatus());
    }

    @Test
    void testCompleteOrder_InvalidTransition() {
        assertThrows(IllegalStateException.class, () -> state.completeOrder());
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
        order.setState(new ProcessingOrderState(order));
        state = order.getState();
        assertTrue(state instanceof ProcessingOrderState);
    }

    @Test
    void testConfirmOrder_InvalidTransition() {
        assertThrows(IllegalStateException.class, () -> state.confirmOrder());
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
        assertDoesNotThrow(() -> state.completeOrder());
        assertTrue(order.getState() instanceof CompletedOrderState);
    }

    @Test
    void testGetStatusString() {
        assertEquals("COMPLETED", state.getStatus());
    }
}