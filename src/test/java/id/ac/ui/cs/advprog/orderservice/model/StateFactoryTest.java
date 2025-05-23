package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateFactoryTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order("DummyTable");
    }

    @Test
    void testStateFactory_createState_processing() {
        OrderState state = StateFactory.createState("PROCESSING", order);
        assertNotNull(state);
        assertTrue(state instanceof ProcessingOrderState);
    }

    @Test
    void testStateFactory_createState_completed() {
        OrderState state = StateFactory.createState("COMPLETED", order);
        assertNotNull(state);
        assertTrue(state instanceof CompletedOrderState);
    }

    @Test
    void testStateFactory_createState_invalidStatus_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                StateFactory.createState("INVALID_STATUS", order));
    }
}