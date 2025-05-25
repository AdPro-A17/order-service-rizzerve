package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutItemResponseTest {

    @Test
    void builder_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        String menuItemName = "Test Menu";
        int quantity = 2;
        double price = 15.0;
        double subtotal = 30.0;

        CheckoutItemResponse response = CheckoutItemResponse.builder()
                .id(id)
                .menuItemId(menuItemId)
                .menuItemName(menuItemName)
                .quantity(quantity)
                .price(price)
                .subtotal(subtotal)
                .build();

        assertEquals(id, response.getId());
        assertEquals(menuItemId, response.getMenuItemId());
        assertEquals(menuItemName, response.getMenuItemName());
        assertEquals(quantity, response.getQuantity());
        assertEquals(price, response.getPrice());
        assertEquals(subtotal, response.getSubtotal());
    }

    @Test
    void allArgsConstructor_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        String menuItemName = "Test Menu";
        int quantity = 2;
        double price = 15.0;
        double subtotal = 30.0;

        CheckoutItemResponse response = new CheckoutItemResponse(id, menuItemId, menuItemName, quantity, price, subtotal);

        assertEquals(id, response.getId());
        assertEquals(menuItemId, response.getMenuItemId());
        assertEquals(menuItemName, response.getMenuItemName());
        assertEquals(quantity, response.getQuantity());
        assertEquals(price, response.getPrice());
        assertEquals(subtotal, response.getSubtotal());
    }

    @Test
    void noArgsConstructor_WorksCorrectly() {
        CheckoutItemResponse response = new CheckoutItemResponse();
        response.setId(UUID.randomUUID());
        response.setQuantity(5);

        assertEquals(5, response.getQuantity());
    }
}