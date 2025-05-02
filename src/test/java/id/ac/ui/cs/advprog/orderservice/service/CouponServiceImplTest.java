package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CouponValidationResponse;
import id.ac.ui.cs.advprog.orderservice.model.Coupon;
import id.ac.ui.cs.advprog.orderservice.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon fixedAmountCoupon;
    private Coupon percentageCoupon;
    private Coupon expiredCoupon;
    private Coupon futureCoupon;
    private Coupon minimumPurchaseCoupon;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        fixedAmountCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("FIX5000")
                .discountAmount(new BigDecimal("5000"))
                .discountPercentage(null)
                .minimumPurchase(new BigDecimal("20000"))
                .validFrom(now.minusDays(10))
                .validUntil(now.plusDays(20))
                .active(true)
                .build();

        percentageCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("PERCENT10")
                .discountAmount(null)
                .discountPercentage(10)
                .minimumPurchase(new BigDecimal("15000"))
                .validFrom(now.minusDays(5))
                .validUntil(now.plusDays(25))
                .active(true)
                .build();

        expiredCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("EXPIRED")
                .discountAmount(new BigDecimal("3000"))
                .discountPercentage(null)
                .minimumPurchase(BigDecimal.ZERO)
                .validFrom(now.minusDays(30))
                .validUntil(now.minusDays(1))
                .active(true)
                .build();

        futureCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("FUTURE")
                .discountAmount(new BigDecimal("2000"))
                .discountPercentage(null)
                .minimumPurchase(BigDecimal.ZERO)
                .validFrom(now.plusDays(5))
                .validUntil(now.plusDays(15))
                .active(true)
                .build();

        minimumPurchaseCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("MIN50000")
                .discountAmount(new BigDecimal("10000"))
                .discountPercentage(null)
                .minimumPurchase(new BigDecimal("50000"))
                .validFrom(now.minusDays(5))
                .validUntil(now.plusDays(25))
                .active(true)
                .build();
    }

    @Test
    void testValidateFixedAmountCoupon_Success() {
        when(couponRepository.findByCodeAndActiveTrue("FIX5000")).thenReturn(Optional.of(fixedAmountCoupon));

        CouponValidationResponse response = couponService.validateCoupon("FIX5000", new BigDecimal("25000"));

        assertTrue(response.isValid());
        assertEquals("Coupon applied successfully", response.getMessage());
        assertEquals(new BigDecimal("5000"), response.getDiscountAmount());

        verify(couponRepository).findByCodeAndActiveTrue("FIX5000");
    }

    @Test
    void testValidatePercentageCoupon_Success() {
        when(couponRepository.findByCodeAndActiveTrue("PERCENT10")).thenReturn(Optional.of(percentageCoupon));

        CouponValidationResponse response = couponService.validateCoupon("PERCENT10", new BigDecimal("20000"));

        assertTrue(response.isValid());
        assertEquals("Coupon applied successfully", response.getMessage());
        assertEquals(new BigDecimal("2000.00"), response.getDiscountAmount());

        verify(couponRepository).findByCodeAndActiveTrue("PERCENT10");
    }

    @Test
    void testValidateCoupon_NotFound() {
        when(couponRepository.findByCodeAndActiveTrue("NONEXISTENT")).thenReturn(Optional.empty());

        CouponValidationResponse response = couponService.validateCoupon("NONEXISTENT", new BigDecimal("20000"));

        assertFalse(response.isValid());
        assertEquals("Coupon not found or inactive", response.getMessage());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());

        verify(couponRepository).findByCodeAndActiveTrue("NONEXISTENT");
    }

    @Test
    void testValidateCoupon_Expired() {
        when(couponRepository.findByCodeAndActiveTrue("EXPIRED")).thenReturn(Optional.of(expiredCoupon));

        CouponValidationResponse response = couponService.validateCoupon("EXPIRED", new BigDecimal("20000"));

        assertFalse(response.isValid());
        assertEquals("Coupon has expired", response.getMessage());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());

        verify(couponRepository).findByCodeAndActiveTrue("EXPIRED");
    }

    @Test
    void testValidateCoupon_NotYetValid() {
        when(couponRepository.findByCodeAndActiveTrue("FUTURE")).thenReturn(Optional.of(futureCoupon));

        CouponValidationResponse response = couponService.validateCoupon("FUTURE", new BigDecimal("20000"));

        assertFalse(response.isValid());
        assertEquals("Coupon is not yet valid", response.getMessage());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());

        verify(couponRepository).findByCodeAndActiveTrue("FUTURE");
    }

    @Test
    void testValidateCoupon_MinimumPurchaseNotMet() {
        when(couponRepository.findByCodeAndActiveTrue("MIN50000")).thenReturn(Optional.of(minimumPurchaseCoupon));

        CouponValidationResponse response = couponService.validateCoupon("MIN50000", new BigDecimal("40000"));

        assertFalse(response.isValid());
        assertEquals("Order amount does not meet minimum purchase requirement", response.getMessage());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());

        verify(couponRepository).findByCodeAndActiveTrue("MIN50000");
    }

    @Test
    void testFixedDiscountGreaterThanOrderAmount() {
        Coupon largeCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("LARGE50000")
                .discountAmount(new BigDecimal("50000"))
                .discountPercentage(null)
                .minimumPurchase(new BigDecimal("10000"))
                .validFrom(LocalDateTime.now().minusDays(10))
                .validUntil(LocalDateTime.now().plusDays(10))
                .active(true)
                .build();

        when(couponRepository.findByCodeAndActiveTrue("LARGE50000")).thenReturn(Optional.of(largeCoupon));

        CouponValidationResponse response = couponService.validateCoupon("LARGE50000", new BigDecimal("30000"));

        assertTrue(response.isValid());
        assertEquals("Coupon applied successfully", response.getMessage());
        assertEquals(new BigDecimal("30000"), response.getDiscountAmount());
    }
}