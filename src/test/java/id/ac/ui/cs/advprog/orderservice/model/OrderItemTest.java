package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
    }

    @Test
    void testOrderItemConstructor() {
        assertNotNull(orderItem.getId(), "ID should be automatically generated");
    }

    @Test
    void testSetAndGetId() {
        UUID newId = UUID.randomUUID();
        orderItem.setId(newId);
        assertEquals(newId, orderItem.getId(), "ID getter should return the set ID");
    }

    @Test
    void testSetAndGetMenuItemId() {
        UUID menuItemId = UUID.randomUUID();
        orderItem.setMenuItemId(menuItemId);
        assertEquals(menuItemId, orderItem.getMenuItemId(), "MenuItemId getter should return the set ID");
    }

    @Test
    void testSetAndGetMenuItemName() {
        String name = "Cheese Burger";
        orderItem.setMenuItemName(name);
        assertEquals(name, orderItem.getMenuItemName(), "MenuItemName getter should return the set name");
    }

    @Test
    void testSetAndGetQuantity() {
        int quantity = 3;
        orderItem.setQuantity(quantity);
        assertEquals(quantity, orderItem.getQuantity(), "Quantity getter should return the set value");
    }

    @Test
    void testSetAndGetPrice() {
        double price = 45000.0;
        orderItem.setPrice(price);
        assertEquals(price, orderItem.getPrice(), "Price getter should return the set value");
    }

    @Test
    void testSetAndGetSubtotal() {
        double subtotal = 135000.0;
        orderItem.setSubtotal(subtotal);
        assertEquals(subtotal, orderItem.getSubtotal(), "Subtotal getter should return the set value");
    }
}