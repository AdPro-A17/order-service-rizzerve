package id.ac.ui.cs.advprog.orderservice.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class RegularPricingTest {

    private RegularPricing regularPricing;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        regularPricing = new RegularPricing();

        OrderItem item1 = new OrderItem();
        item1.setMenuItemId(UUID.randomUUID());
        item1.setMenuItemName("Nasi Goreng");
        item1.setQuantity(2);
        item1.setPrice(25000.0);
        item1.setSubtotal(50000.0);

        OrderItem item2 = new OrderItem();
        item2.setMenuItemId(UUID.randomUUID());
        item2.setMenuItemName("Es Teh");
        item2.setQuantity(2);
        item2.setPrice(5000.0);
        item2.setSubtotal(10000.0);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(item1);
        orderItems.add(item2);

        checkout = new Checkout();
        checkout.setId(UUID.randomUUID());
        checkout.setTableNumber("A1");
        checkout.setOrderItems(orderItems);
        checkout.setTotalPrice(60000.0);
    }

    @Test
    void calculateFinalPrice_ShouldSetZeroDiscountAndTotalPriceAsFinalPrice() {
        regularPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(60000.0, checkout.getFinalPrice());
        assertEquals(checkout.getTotalPrice(), checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithZeroTotalPrice_ShouldSetZeroFinalPrice() {
        checkout.setTotalPrice(0.0);
        regularPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(0.0, checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithNegativeTotalPrice_ShouldHandleNegativeValues() {
        checkout.setTotalPrice(-1000.0);
        regularPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(-1000.0, checkout.getFinalPrice());
    }
}
