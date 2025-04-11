package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private OrderItem orderItem;
    private UUID id;
    private UUID menuItemId;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        menuItemId = UUID.randomUUID();
        orderItem = new OrderItem();
        // Ensure OrderItem model exists
        fail("OrderItem model needed.");
    }

    @Test
    void testDefaultConstructorGeneratesId() {
        OrderItem newItem = new OrderItem();
        assertNotNull(newItem.getId());
    }

    @Test
    void testGetSetId() {
        orderItem.setId(id);
        assertEquals(id, orderItem.getId());
    }

    @Test
    void testGetSetMenuItemId() {
        orderItem.setMenuItemId(menuItemId);
        assertEquals(menuItemId, orderItem.getMenuItemId());
    }

    @Test
    void testGetSetMenuItemName() {
        String name = "Nasi Goreng";
        orderItem.setMenuItemName(name);
        assertEquals(name, orderItem.getMenuItemName());
    }

    @Test
    void testGetSetQuantity() {
        int quantity = 2;
        orderItem.setQuantity(quantity);
        assertEquals(quantity, orderItem.getQuantity());
        // Should also test calculation of subtotal when quantity changes
        // orderItem.setPrice(10.0);
        // orderItem.calculateSubtotal(); // Assuming such a method exists or is done in setQuantity/setPrice
        // assertEquals(20.0, orderItem.getSubtotal());
        fail("Subtotal calculation logic needed in OrderItem.");
    }

    @Test
    void testGetSetPrice() {
        double price = 15000.50;
        orderItem.setPrice(price);
        assertEquals(price, orderItem.getPrice());
        // Should also test calculation of subtotal when price changes
        // orderItem.setQuantity(1);
        // orderItem.calculateSubtotal(); // Assuming such a method exists or is done in setQuantity/setPrice
        // assertEquals(15000.50, orderItem.getSubtotal());
        fail("Subtotal calculation logic needed in OrderItem.");
    }

    @Test
    void testGetSetSubtotal() {
        // Subtotal is typically calculated, not set directly, but test getter/setter if they exist.
        double subtotal = 30.0;
        orderItem.setSubtotal(subtotal); // If setter exists
        assertEquals(subtotal, orderItem.getSubtotal());
    }
}