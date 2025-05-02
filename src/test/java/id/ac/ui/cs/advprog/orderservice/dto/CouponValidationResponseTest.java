package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CouponValidationResponseTest {

    @Test
    void builderShouldCreateResponseWithCorrectValues() {
        boolean valid = true;
        String message = "Coupon applied successfully";
        BigDecimal discountAmount = new BigDecimal("5000");

        CouponValidationResponse response = CouponValidationResponse.builder()
                .valid(valid)
                .message(message)
                .discountAmount(discountAmount)
                .build();

        assertEquals(valid, response.isValid());
        assertEquals(message, response.getMessage());
        assertEquals(discountAmount, response.getDiscountAmount());
    }
}