package id.ac.ui.cs.advprog.orderservice.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testOrderNotFoundException_WithOrderId() {
        UUID orderId = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(orderId);
        
        assertEquals("Could not find order with ID: " + orderId, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testOrderItemNotFoundException_WithItemIdAndOrderId() {
        UUID itemId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderItemNotFoundException exception = new OrderItemNotFoundException(itemId, orderId);
        
        assertEquals("Could not find order item with ID: " + itemId + " in order with ID: " + orderId, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testTableNotAvailableException_WithMessage() {
        String message = "Table 5 is not available";
        TableNotAvailableException exception = new TableNotAvailableException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMenuItemNotFoundException_WithMenuItemId() {
        UUID menuItemId = UUID.randomUUID();
        MenuItemNotFoundException exception = new MenuItemNotFoundException(menuItemId);
        
        assertEquals("Menu item not found with ID: " + menuItemId, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMenuItemNotFoundException_WithMenuItemIdAndReason() {
        UUID menuItemId = UUID.randomUUID();
        String reason = "Item is out of stock";
        MenuItemNotFoundException exception = new MenuItemNotFoundException(menuItemId, reason);
        
        assertEquals("Menu item " + menuItemId + " is not available: " + reason, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionsAreRuntimeExceptions() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        
        assertTrue(new OrderNotFoundException(orderId) instanceof RuntimeException);
        assertTrue(new OrderItemNotFoundException(itemId, orderId) instanceof RuntimeException);
        assertTrue(new TableNotAvailableException("test") instanceof RuntimeException);
        assertTrue(new MenuItemNotFoundException(menuItemId) instanceof RuntimeException);
    }
} 