package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.exception.CouponApplicationException;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponPricingTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CouponPricing couponPricing;

    private Checkout checkout;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(couponPricing, "couponServiceBaseUrl", "http://localhost:8081");

        checkout = new Checkout();
        checkout.setTotalPrice(100.0);
        checkout.setCouponCode("DISCOUNT10");
    }

    @Test
    void calculateTotal_WithValidCoupon_AppliesDiscount() {
        BigDecimal discountedPrice = new BigDecimal("90.00");
        when(restTemplate.postForObject(anyString(), isNull(), eq(BigDecimal.class)))
                .thenReturn(discountedPrice);

        couponPricing.calculateTotal(checkout);

        assertEquals(90.0, checkout.getTotalPrice());
        assertEquals(10.0, checkout.getDiscountAmount());
        verify(restTemplate).postForObject(contains("/coupon/DISCOUNT10/apply"), isNull(), eq(BigDecimal.class));
    }

    @Test
    void calculateTotal_WithNullCouponCode_UsesRegularPricing() {
        checkout.setCouponCode(null);

        couponPricing.calculateTotal(checkout);

        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(100.0, checkout.getTotalPrice());
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    void calculateTotal_WithEmptyCouponCode_UsesRegularPricing() {
        checkout.setCouponCode("");

        couponPricing.calculateTotal(checkout);

        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(100.0, checkout.getTotalPrice());
        verify(restTemplate, never()).postForObject(anyString(), any(), any());
    }

    @Test
    void calculateTotal_CouponServiceReturnsNull_UsesRegularPricing() {
        when(restTemplate.postForObject(anyString(), isNull(), eq(BigDecimal.class)))
                .thenReturn(null);

        couponPricing.calculateTotal(checkout);

        assertEquals(0.0, checkout.getDiscountAmount());
        assertEquals(100.0, checkout.getTotalPrice());
    }

    @Test
    void calculateTotal_HttpClientErrorException_ThrowsCouponApplicationException() {
        HttpClientErrorException httpException = mock(HttpClientErrorException.class);
        when(httpException.getResponseBodyAsString()).thenReturn("Invalid coupon");
        when(restTemplate.postForObject(anyString(), isNull(), eq(BigDecimal.class)))
                .thenThrow(httpException);

        CouponApplicationException exception = assertThrows(CouponApplicationException.class,
                () -> couponPricing.calculateTotal(checkout));

        assertTrue(exception.getMessage().contains("Failed to apply coupon: Invalid coupon"));
        assertEquals(httpException, exception.getCause());
    }

    @Test
    void calculateTotal_GenericException_ThrowsCouponApplicationException() {
        RuntimeException runtimeException = new RuntimeException("Connection timeout");
        when(restTemplate.postForObject(anyString(), isNull(), eq(BigDecimal.class)))
                .thenThrow(runtimeException);

        CouponApplicationException exception = assertThrows(CouponApplicationException.class,
                () -> couponPricing.calculateTotal(checkout));

        assertTrue(exception.getMessage().contains("Failed to apply coupon: Connection timeout"));
        assertEquals(runtimeException, exception.getCause());
    }
}