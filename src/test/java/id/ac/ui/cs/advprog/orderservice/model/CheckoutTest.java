package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutTest {

    @Test
    void constructor_SetsCreatedAtToCurrentTime() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Checkout checkout = new Checkout();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(checkout.getCreatedAt());
        assertTrue(checkout.getCreatedAt().isAfter(before));
        assertTrue(checkout.getCreatedAt().isBefore(after));
    }

    @Test
    void setItems_WithValidList_SetsItems() {
        Checkout checkout = new Checkout();
        OrderItem item1 = new OrderItem();
        item1.setId(UUID.randomUUID());
        OrderItem item2 = new OrderItem();
        item2.setId(UUID.randomUUID());

        List<OrderItem> items = List.of(item1, item2);
        checkout.setItems(items);

        assertEquals(2, checkout.getItems().size());
        assertTrue(checkout.getItems().contains(item1));
        assertTrue(checkout.getItems().contains(item2));
    }

    @Test
    void setItems_WithNullList_ClearsItems() {
        Checkout checkout = new Checkout();
        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        checkout.getItems().add(item);

        checkout.setItems(null);

        assertTrue(checkout.getItems().isEmpty());
    }

    @Test
    void setItems_ClearsExistingItems() {
        Checkout checkout = new Checkout();
        OrderItem existingItem = new OrderItem();
        existingItem.setId(UUID.randomUUID());
        checkout.getItems().add(existingItem);

        OrderItem newItem = new OrderItem();
        newItem.setId(UUID.randomUUID());
        checkout.setItems(List.of(newItem));

        assertEquals(1, checkout.getItems().size());
        assertTrue(checkout.getItems().contains(newItem));
        assertFalse(checkout.getItems().contains(existingItem));
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        Checkout checkout = new Checkout();
        UUID id = UUID.randomUUID();
        String couponCode = "DISCOUNT10";
        double totalPrice = 100.0;
        double discountAmount = 10.0;
        int tableNumber = 5;

        checkout.setId(id);
        checkout.setCouponCode(couponCode);
        checkout.setTotalPrice(totalPrice);
        checkout.setDiscountAmount(discountAmount);
        checkout.setTableNumber(tableNumber);

        assertEquals(id, checkout.getId());
        assertEquals(couponCode, checkout.getCouponCode());
        assertEquals(totalPrice, checkout.getTotalPrice());
        assertEquals(discountAmount, checkout.getDiscountAmount());
        assertEquals(tableNumber, checkout.getTableNumber());
    }
}