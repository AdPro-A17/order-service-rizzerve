package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @CsvSource({
            "SAVE10, 6000.0, 54000.0",
            "SAVE20, 12000.0, 48000.0",
            "HALF, 30000.0, 30000.0"
    })
    void calculateFinalPrice_WithValidCoupon_ShouldApplyCorrectDiscount(
            String couponCode, double expectedDiscountAmount, double expectedFinalPrice) {
        checkout.setCouponCode(couponCode);
        couponPricing.calculateFinalPrice(checkout);
        assertEquals(expectedDiscountAmount, checkout.getDiscountAmount());
        assertEquals(expectedFinalPrice, checkout.getFinalPrice());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "WRONGCODE", "EXPIRED"})
    void calculateFinalPrice_WithInvalidOrEmptyCoupon_ShouldNotApplyDiscount(String couponCode) {
        checkout.setCouponCode(couponCode);
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
