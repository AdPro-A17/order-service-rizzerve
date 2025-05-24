package id.ac.ui.cs.advprog.orderservice.service;
/* // Temporarily commented out
import id.ac.ui.cs.advprog.orderservice.dto.CouponValidationResponse;
import id.ac.ui.cs.advprog.orderservice.model.Coupon;
import id.ac.ui.cs.advprog.orderservice.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public CouponValidationResponse validateCoupon(String couponCode, BigDecimal orderAmount) {
        var couponOptional = couponRepository.findByCodeAndActiveTrue(couponCode);

        if (couponOptional.isEmpty()) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Coupon not found or inactive")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        Coupon coupon = couponOptional.get();
        LocalDateTime now = LocalDateTime.now();

        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Coupon is not yet valid")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Coupon has expired")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getMinimumPurchase() != null && orderAmount.compareTo(coupon.getMinimumPurchase()) < 0) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Order amount does not meet minimum purchase requirement")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal discountAmount = calculateDiscountAmount(coupon, orderAmount);

        return CouponValidationResponse.builder()
                .valid(true)
                .message("Coupon applied successfully")
                .discountAmount(discountAmount)
                .build();
    }

    private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (coupon.getDiscountAmount() != null) {
            discountAmount = coupon.getDiscountAmount();
        } else if (coupon.getDiscountPercentage() != null) {
            discountAmount = orderAmount
                    .multiply(BigDecimal.valueOf(coupon.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return discountAmount.min(orderAmount);
    }
}
*/