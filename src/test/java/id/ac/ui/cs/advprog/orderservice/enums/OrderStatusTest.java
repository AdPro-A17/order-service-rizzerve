package id.ac.ui.cs.advprog.orderservice.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderStatusTest {

    @Test
    void enumValues_ShouldHaveCorrectOrdinals() {
        assertEquals(0, OrderStatus.PENDING.ordinal());
        assertEquals(1, OrderStatus.PROCESSING.ordinal());
        assertEquals(2, OrderStatus.COMPLETED.ordinal());
        assertEquals(3, OrderStatus.CANCELLED.ordinal());
    }

    @Test
    void valueOf_ShouldReturnCorrectEnum() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.PROCESSING, OrderStatus.valueOf("PROCESSING"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.valueOf("COMPLETED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }
}