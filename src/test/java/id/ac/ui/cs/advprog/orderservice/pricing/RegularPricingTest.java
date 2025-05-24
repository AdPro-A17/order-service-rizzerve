package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegularPricingTest {
    private RegularPricing regularPricing;
    private Checkout checkout;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        regularPricing = new RegularPricing();
        checkout = new Checkout();
        items = new ArrayList<>();

        OrderItem item1 = new OrderItem();
        item1.setMenuItemId(UUID.randomUUID());
        item1.setMenuItemName("Burger");
        item1.setQuantity(2);
        item1.setPrice(50000.0);
        items.add(item1);

        OrderItem item2 = new OrderItem();
        item2.setMenuItemId(UUID.randomUUID());
        item2.setMenuItemName("Fries");
        item2.setQuantity(1);
        item2.setPrice(25000.0);
        items.add(item2);

        checkout.setItems(items);
    }

    @Test
    void testCalculateTotal() {
        regularPricing.calculateTotal(checkout);

        assertEquals(125000.0, checkout.getTotalPrice());
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(125000.0, checkout.getFinalPrice());
    }

    @Test
    void testCalculateTotalWithEmptyItems() {
        checkout.setItems(new ArrayList<>());
        regularPricing.calculateTotal(checkout);

        assertEquals(0.0, checkout.getTotalPrice());
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(0.0, checkout.getFinalPrice());
    }

    @Test
    void testCalculateTotalWithZeroPrice() {
        items.get(0).setPrice(0.0);
        items.get(1).setPrice(0.0);
        regularPricing.calculateTotal(checkout);

        assertEquals(0.0, checkout.getTotalPrice());
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(0.0, checkout.getFinalPrice());
    }
}