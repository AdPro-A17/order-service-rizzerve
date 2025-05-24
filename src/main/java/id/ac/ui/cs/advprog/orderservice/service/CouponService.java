package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CouponValidationResponse;
import java.math.BigDecimal;

public interface CouponService {
    CouponValidationResponse validateCoupon(String couponCode, BigDecimal orderAmount);
}