package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutTest {
    private Checkout checkout;
    private List<OrderItem> orderItems;
    private OrderItem item1;
    private OrderItem item2;

    @BeforeEach
    public void setUp() {
        checkout = new Checkout();
        orderItems = new ArrayList<>();

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

        orderItems.add(item1);
        orderItems.add(item2);
    }

    @Test
    void testCheckoutConstructor() {
        assertNotNull(checkout.getId(), "ID should be automatically generated");
    }

    @Test
    void testSetAndGetId() {
        UUID newId = UUID.randomUUID();
        checkout.setId(newId);
        assertEquals(newId, checkout.getId(), "ID getter should return the set ID");
    }

    @Test
    void testSetAndGetOrderItems() {
        checkout.setOrderItems(orderItems);
        assertEquals(orderItems, checkout.getOrderItems(), "OrderItems getter should return the set items");
        assertEquals(2, checkout.getOrderItems().size(), "OrderItems size should match");
    }

    @Test
    void testSetAndGetTotalPrice() {
        double totalPrice = 125000.0;
        checkout.setTotalPrice(totalPrice);
        assertEquals(totalPrice, checkout.getTotalPrice(), "TotalPrice getter should return the set value");
    }

    @Test
    void testSetAndGetCouponCode() {
        String couponCode = "SAVE10";
        checkout.setCouponCode(couponCode);
        assertEquals(couponCode, checkout.getCouponCode(), "CouponCode getter should return the set code");
    }

    @Test
    void testSetAndGetDiscountAmount() {
        double discountAmount = 12500.0;
        checkout.setDiscountAmount(discountAmount);
        assertEquals(discountAmount, checkout.getDiscountAmount(), "DiscountAmount getter should return the set value");
    }

    @Test
    void testSetAndGetFinalPrice() {
        double finalPrice = 112500.0;
        checkout.setFinalPrice(finalPrice);
        assertEquals(finalPrice, checkout.getFinalPrice(), "FinalPrice getter should return the set value");
    }

    @Test
    void testSetAndGetTableNumber() {
        String tableNumber = "A12";
        checkout.setTableNumber(tableNumber);
        assertEquals(tableNumber, checkout.getTableNumber(), "TableNumber getter should return the set value");
    }

    @Test
    void testSetAndGetCustomerId() {
        UUID customerId = UUID.randomUUID();
        checkout.setCustomerId(customerId);
        assertEquals(customerId, checkout.getCustomerId(), "CustomerId getter should return the set ID");
    }
}