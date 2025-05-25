package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutResponseTest {

    @Test
    void builder_WorksCorrectly() {
        UUID checkoutId = UUID.randomUUID();
        int tableNumber = 5;
        double totalPrice = 100.0;
        String couponCode = "DISCOUNT10";
        double discountAmount = 10.0;

        CheckoutItemResponse item = CheckoutItemResponse.builder()
                .id(UUID.randomUUID())
                .menuItemId(UUID.randomUUID())
                .menuItemName("Test Menu")
                .quantity(2)
                .price(15.0)
                .subtotal(30.0)
                .build();

        List<CheckoutItemResponse> items = List.of(item);

        CheckoutResponse response = CheckoutResponse.builder()
                .checkoutId(checkoutId)
                .tableNumber(tableNumber)
                .items(items)
                .totalPrice(totalPrice)
                .couponCode(couponCode)
                .discountAmount(discountAmount)
                .build();

        assertEquals(checkoutId, response.getCheckoutId());
        assertEquals(tableNumber, response.getTableNumber());
        assertEquals(items, response.getItems());
        assertEquals(totalPrice, response.getTotalPrice());
        assertEquals(couponCode, response.getCouponCode());
        assertEquals(discountAmount, response.getDiscountAmount());
    }

    @Test
    void noArgsConstructor_WorksCorrectly() {
        CheckoutResponse response = new CheckoutResponse();
        response.setCheckoutId(UUID.randomUUID());
        response.setTableNumber(5);

        assertEquals(5, response.getTableNumber());
    }
}