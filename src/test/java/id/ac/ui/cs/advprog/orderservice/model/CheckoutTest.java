package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutTest {

    @Test
    void builderShouldCreateCheckoutWithCorrectValues() {
        // Arrange
        UUID id = UUID.randomUUID();
        Order order = new Order();
        UUID userId = UUID.randomUUID();
        LocalDateTime checkoutTime = LocalDateTime.now();
        boolean successful = true;
        String notes = "Test notes";

        Checkout checkout = Checkout.builder()
                .id(id)
                .order(order)
                .userId(userId)
                .checkoutTime(checkoutTime)
                .successful(successful)
                .notes(notes)
                .build();

        assertEquals(id, checkout.getId());
        assertEquals(order, checkout.getOrder());
        assertEquals(userId, checkout.getUserId());
        assertEquals(checkoutTime, checkout.getCheckoutTime());
        assertEquals(successful, checkout.isSuccessful());
        assertEquals(notes, checkout.getNotes());
    }
}