package id.ac.ui.cs.advprog.orderservice.dto;

import id.ac.ui.cs.advprog.orderservice.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutResponseTest {

    @Test
    void builderShouldCreateResponseWithCorrectValues() {
        UUID orderId = UUID.randomUUID();
        UUID checkoutId = UUID.randomUUID();
        String tableNumber = "A1";
        LocalDateTime orderTime = LocalDateTime.now();
        OrderStatus status = OrderStatus.PENDING;
        BigDecimal subtotal = new BigDecimal("50000");
        BigDecimal discount = new BigDecimal("5000");
        BigDecimal total = new BigDecimal("45000");
        String appliedCouponCode = "TEST10";
        List<OrderItemResponse> items = new ArrayList<>();
        LocalDateTime checkoutTime = LocalDateTime.now();
        boolean successful = true;
        String notes = "Test notes";

        CheckoutResponse response = CheckoutResponse.builder()
                .orderId(orderId)
                .checkoutId(checkoutId)
                .tableNumber(tableNumber)
                .orderTime(orderTime)
                .status(status)
                .subtotal(subtotal)
                .discount(discount)
                .total(total)
                .appliedCouponCode(appliedCouponCode)
                .items(items)
                .checkoutTime(checkoutTime)
                .successful(successful)
                .notes(notes)
                .build();

        assertEquals(orderId, response.getOrderId());
        assertEquals(checkoutId, response.getCheckoutId());
        assertEquals(tableNumber, response.getTableNumber());
        assertEquals(orderTime, response.getOrderTime());
        assertEquals(status, response.getStatus());
        assertEquals(subtotal, response.getSubtotal());
        assertEquals(discount, response.getDiscount());
        assertEquals(total, response.getTotal());
        assertEquals(appliedCouponCode, response.getAppliedCouponCode());
        assertEquals(items, response.getItems());
        assertEquals(checkoutTime, response.getCheckoutTime());
        assertEquals(successful, response.isSuccessful());
        assertEquals(notes, response.getNotes());
    }
}