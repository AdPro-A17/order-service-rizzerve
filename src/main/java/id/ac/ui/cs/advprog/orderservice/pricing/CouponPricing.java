package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CouponPricing implements CheckoutPricing{
    private final Map<String, Double> couponDiscounts = new HashMap<>();

    public CouponPricing() {
        couponDiscounts.put("SAVE10", 0.1);
        couponDiscounts.put("SAVE20", 0.2);
        couponDiscounts.put("HALF", 0.5);
    }

    @Override
    public void calculateFinalPrice(Checkout checkout) {
        String couponCode = checkout.getCouponCode();

        if (couponCode != null && !couponCode.isEmpty() && couponDiscounts.containsKey(couponCode)) {
            double discountRate = couponDiscounts.get(couponCode);
            double discountAmount = checkout.getTotalPrice() * discountRate;

            checkout.setDiscountAmount(discountAmount);
            checkout.setFinalPrice(checkout.getTotalPrice() - discountAmount);
        } else {

            checkout.setDiscountAmount(0);
            checkout.setFinalPrice(checkout.getTotalPrice());
        }
    }

    public boolean isValidCoupon(String couponCode) {
        return couponCode != null && couponDiscounts.containsKey(couponCode);
    }
}