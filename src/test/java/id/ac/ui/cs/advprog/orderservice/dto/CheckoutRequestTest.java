package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutRequestTest {

    @Test
    void gettersAndSetters_WorkCorrectly() {
        CheckoutRequest request = new CheckoutRequest();
        UUID orderId = UUID.randomUUID();
        String couponCode = "DISCOUNT10";

        request.setOrderId(orderId);
        request.setCouponCode(couponCode);

        assertEquals(orderId, request.getOrderId());
        assertEquals(couponCode, request.getCouponCode());
    }
}