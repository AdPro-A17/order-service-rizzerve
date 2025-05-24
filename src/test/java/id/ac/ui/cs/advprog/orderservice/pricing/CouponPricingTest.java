package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponPricingTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CouponPricing couponPricing;

    private Checkout checkout;
    private List<OrderItem> items;
    private static final String COUPON_SERVICE_URL = "http://localhost:8081";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(couponPricing, "couponServiceUrl", COUPON_SERVICE_URL);

        checkout = new Checkout();
        items = new ArrayList<>();

        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Burger");
        item.setQuantity(2);
        item.setPrice(50000.0);
        items.add(item);

        checkout.setItems(items);
        checkout.setTotalPrice(100000.0);
        checkout.setCouponCode("SAVE10");
    }

    @Test
    void testCalculateTotalWithValidCoupon() {
        when(restTemplate.postForObject(
                eq(COUPON_SERVICE_URL + "/coupon/SAVE10/apply?total=100000.0"),
                eq(null),
                eq(Double.class)
        )).thenReturn(90000.0);

        couponPricing.calculateTotal(checkout);

        assertEquals(100000.0, checkout.getTotalPrice());
        assertEquals(10000.0, checkout.getDiscountAmount());
        assertEquals(90000.0, checkout.getFinalPrice());
        verify(restTemplate).postForObject(
                eq(COUPON_SERVICE_URL + "/coupon/SAVE10/apply?total=100000.0"),
                eq(null),
                eq(Double.class)
        );
    }

    @Test
    void testCalculateTotalWithInvalidCoupon() {
        when(restTemplate.postForObject(
                any(String.class),
                eq(null),
                eq(Double.class)
        )).thenThrow(new RuntimeException("Coupon not found"));

        couponPricing.calculateTotal(checkout);

        assertEquals(100000.0, checkout.getTotalPrice());
        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(100000.0, checkout.getFinalPrice());
    }
}