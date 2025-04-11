package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CouponPricingTest {

    private CouponPricing couponPricing;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        couponPricing = new CouponPricing();

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
    void calculateFinalPrice_WithValidSAVE10Coupon_ShouldApply10PercentDiscount() {
        checkout.setCouponCode("SAVE10");
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(6000.0, checkout.getDiscountAmount());
        assertEquals(54000.0, checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithValidSAVE20Coupon_ShouldApply20PercentDiscount() {
        checkout.setCouponCode("SAVE20");
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(12000.0, checkout.getDiscountAmount());
        assertEquals(48000.0, checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithValidHALFCoupon_ShouldApply50PercentDiscount() {
        checkout.setCouponCode("HALF");
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(30000.0, checkout.getDiscountAmount());
        assertEquals(30000.0, checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithInvalidCoupon_ShouldNotApplyDiscount() {
        checkout.setCouponCode("INVALID");
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(60000.0, checkout.getFinalPrice());
        assertEquals(checkout.getTotalPrice(), checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithNullCoupon_ShouldNotApplyDiscount() {
        checkout.setCouponCode(null);
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(60000.0, checkout.getFinalPrice());
        assertEquals(checkout.getTotalPrice(), checkout.getFinalPrice());
    }

    @Test
    void calculateFinalPrice_WithEmptyCoupon_ShouldNotApplyDiscount() {
        checkout.setCouponCode("");
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(60000.0, checkout.getFinalPrice());
        assertEquals(checkout.getTotalPrice(), checkout.getFinalPrice());
    }

    @Test
    void isValidCoupon_WithValidCoupons_ShouldReturnTrue() {
        assertTrue(couponPricing.isValidCoupon("SAVE10"));
        assertTrue(couponPricing.isValidCoupon("SAVE20"));
        assertTrue(couponPricing.isValidCoupon("HALF"));
    }

    @Test
    void isValidCoupon_WithInvalidCoupons_ShouldReturnFalse() {
        assertFalse(couponPricing.isValidCoupon("INVALID"));
        assertFalse(couponPricing.isValidCoupon("save10"));
        assertFalse(couponPricing.isValidCoupon("SAVE30"));
    }

    @Test
    void isValidCoupon_WithNullOrEmptyCoupon_ShouldReturnFalse() {
        assertFalse(couponPricing.isValidCoupon(null));
        assertFalse(couponPricing.isValidCoupon(""));
    }

    @Test
    void calculateFinalPrice_WithZeroTotalPrice_ShouldApplyDiscountCorrectly() {
        checkout.setTotalPrice(0.0);
        checkout.setCouponCode("SAVE10");
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(0.0, checkout.getFinalPrice());
    }
}
