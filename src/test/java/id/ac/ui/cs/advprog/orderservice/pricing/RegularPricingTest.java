package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegularPricingTest {

    @Test
    void calculateTotal_SetsDiscountAmountToZero() {
        RegularPricing regularPricing = new RegularPricing();
        Checkout checkout = new Checkout();
        checkout.setTotalPrice(100.0);
        checkout.setDiscountAmount(10.0); // Set initial discount

        regularPricing.calculateTotal(checkout);

        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(100.0, checkout.getTotalPrice()); // Total price unchanged
    }
}