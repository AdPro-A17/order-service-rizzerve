package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemResponseTest {

    @Test
    void builderShouldCreateResponseWithCorrectValues() {
        UUID id = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        String menuItemName = "Test Item";
        BigDecimal pricePerItem = new BigDecimal("10000");
        int quantity = 2;
        BigDecimal subtotal = new BigDecimal("20000");

        OrderItemResponse response = OrderItemResponse.builder()
                .id(id)
                .menuItemId(menuItemId)
                .menuItemName(menuItemName)
                .pricePerItem(pricePerItem)
                .quantity(quantity)
                .subtotal(subtotal)
                .build();

        assertEquals(id, response.getId());
        assertEquals(menuItemId, response.getMenuItemId());
        assertEquals(menuItemName, response.getMenuItemName());
        assertEquals(pricePerItem, response.getPricePerItem());
        assertEquals(quantity, response.getQuantity());
        assertEquals(subtotal, response.getSubtotal());
    }
}