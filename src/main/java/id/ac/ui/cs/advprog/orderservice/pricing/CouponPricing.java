package id.ac.ui.cs.advprog.orderservice.pricing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import java.math.BigDecimal;
import org.springframework.web.client.HttpClientErrorException;
import id.ac.ui.cs.advprog.orderservice.exception.CouponApplicationException;

@Component
@Slf4j
public class CouponPricing implements PricingStrategy {
    private final RestTemplate restTemplate;

    @Value("${coupon-service.url}")
    private String couponServiceBaseUrl;

    public CouponPricing(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void calculateTotal(Checkout checkout) {
        if (checkout.getCouponCode() != null && !checkout.getCouponCode().isEmpty()) {
            BigDecimal originalTotalPrice = BigDecimal.valueOf(checkout.getTotalPrice());
            try {
                String applyCouponUrl = couponServiceBaseUrl + "/coupon/" + checkout.getCouponCode() + "/apply?total=" + originalTotalPrice.toPlainString(); // Use toPlainString for BigDecimal in URL
                BigDecimal discountedPrice = restTemplate.postForObject(
                        applyCouponUrl,
                        null,
                        BigDecimal.class
                );

                if (discountedPrice != null) {
                    BigDecimal discountAmount = originalTotalPrice.subtract(discountedPrice);
                    checkout.setDiscountAmount(discountAmount.doubleValue());
                    checkout.setTotalPrice(discountedPrice.doubleValue());
                } else {
                    new RegularPricing().calculateTotal(checkout);
                }
            } catch (HttpClientErrorException e) {
                log.error("Error Http Client {}: {}", checkout.getCouponCode(), e.getResponseBodyAsString());
                throw new CouponApplicationException("Failed to apply coupon: " + e.getResponseBodyAsString(), e);
            } catch (Exception e) {
                log.error("Error applying coupon {}: {}", checkout.getCouponCode(), e.getMessage());
                throw new CouponApplicationException("Failed to apply coupon: " + e.getMessage(), e);
            }
        } else {
            new RegularPricing().calculateTotal(checkout);
        }
    }
}