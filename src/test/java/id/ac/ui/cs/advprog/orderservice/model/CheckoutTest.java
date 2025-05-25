package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class CheckoutTest {
    private Checkout checkout;
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order("A1");
        checkout = new Checkout(order);
    }

    @Test
    void testCheckoutCreation() {
        assertNotNull(checkout);
        assertEquals(order, checkout.getOrder());
        assertNotNull(checkout.getCheckoutTime());
    }

    @Test
    void testEqualsAndHashCode() {
        Checkout checkout1 = new Checkout(order);
        Checkout checkout2 = new Checkout(order);

        assertNotEquals(checkout1, checkout2); // Different IDs

        UUID sameId = UUID.randomUUID();
        checkout1.setId(sameId);
        checkout2.setId(sameId);

        assertEquals(checkout1, checkout2);
        assertEquals(checkout1.hashCode(), checkout2.hashCode());
    }
}