package id.ac.ui.cs.advprog.orderservice.exception;

import java.util.UUID;

public class MenuItemNotFoundException extends RuntimeException {
    
    public MenuItemNotFoundException(UUID menuItemId) {
        super("Menu item not found with ID: " + menuItemId);
    }
    
    public MenuItemNotFoundException(UUID menuItemId, String reason) {
        super("Menu item " + menuItemId + " is not available: " + reason);
    }
} 