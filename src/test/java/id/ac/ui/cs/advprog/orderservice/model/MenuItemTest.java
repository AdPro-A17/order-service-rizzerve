package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void create_ShouldCreateMenuItemWithCorrectValues() {
        String name = "Test Item";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("10000");
        boolean available = true;
        String category = "Test Category";
        double averageRating = 4.5;
        UUID createdBy = UUID.randomUUID();

        MenuItem menuItem = MenuItem.create(
                name,
                description,
                price,
                available,
                category,
                averageRating,
                createdBy
        );

        assertEquals(name, menuItem.getName());
        assertEquals(description, menuItem.getDescription());
        assertEquals(price, menuItem.getPrice());
        assertEquals(available, menuItem.isAvailable());
        assertEquals(category, menuItem.getCategory());
        assertEquals(averageRating, menuItem.getAverageRating());
        assertEquals(createdBy, menuItem.getCreatedBy());
    }
}