package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutTest {
    private Checkout checkout;
    private List<OrderItem> items;
    private OrderItem item1;
    private OrderItem item2;

    @BeforeEach
    public void setUp() {
        checkout = new Checkout();
        items = new ArrayList<>();

        item1 = new OrderItem();
        item1.setMenuItemId(UUID.randomUUID());
        item1.setMenuItemName("Burger");
        item1.setQuantity(2);
        item1.setPrice(50000.0);
        item1.setSubtotal(100000.0);

        item2 = new OrderItem();
        item2.setMenuItemId(UUID.randomUUID());
        item2.setMenuItemName("Fries");
        item2.setQuantity(1);
        item2.setPrice(25000.0);
        item2.setSubtotal(25000.0);

        items.add(item1);
        items.add(item2);
    }

    @Test
    void testSetAndGetId() {
        UUID newId = UUID.randomUUID();
        checkout.setId(newId);
        assertEquals(newId, checkout.getId());
    }

    @Test
    void testSetAndGetItems() {
        checkout.setItems(items);
        assertEquals(items, checkout.getItems());
        assertEquals(2, checkout.getItems().size());
    }

    @Test
    void testSetAndGetTotalPrice() {
        double totalPrice = 125000.0;
        checkout.setTotalPrice(totalPrice);
        assertEquals(totalPrice, checkout.getTotalPrice());
    }

    @Test
    void testSetAndGetCouponCode() {
        String couponCode = "SAVE10";
        checkout.setCouponCode(couponCode);
        assertEquals(couponCode, checkout.getCouponCode());
    }

    @Test
    void testSetAndGetDiscountAmount() {
        double discountAmount = 12500.0;
        checkout.setDiscountAmount(discountAmount);
        assertEquals(discountAmount, checkout.getDiscountAmount());
    }

    @Test
    void testSetAndGetFinalPrice() {
        double finalPrice = 112500.0;
        checkout.setFinalPrice(finalPrice);
        assertEquals(finalPrice, checkout.getFinalPrice());
    }

    @Test
    void testSetAndGetTableNumber() {
        String tableNumber = "A12";
        checkout.setTableNumber(tableNumber);
        assertEquals(tableNumber, checkout.getTableNumber());
    }

    @Test
    void testSetAndGetStatus() {
        String status = "COMPLETED";
        checkout.setStatus(status);
        assertEquals(status, checkout.getStatus());
    }

    @Test
    void testSetAndGetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        checkout.setCreatedAt(now);
        assertEquals(now, checkout.getCreatedAt());
    }

    @Test
    void testSetAndGetUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        checkout.setUpdatedAt(now);
        assertEquals(now, checkout.getUpdatedAt());
    }
}