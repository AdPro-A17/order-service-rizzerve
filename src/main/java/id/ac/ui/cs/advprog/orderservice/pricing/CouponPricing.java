package id.ac.ui.cs.advprog.orderservice.pricing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;

@Component
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
            try {
                String couponServiceUrl = couponServiceBaseUrl + "/" + checkout.getCouponCode() + "/apply";
                Double discountedPrice = restTemplate.postForObject(
                        couponServiceUrl,
                        checkout.getTotalPrice(),
                        Double.class
                );

                if (discountedPrice != null) {
                    checkout.setDiscountAmount(checkout.getTotalPrice() - discountedPrice);
                    checkout.setFinalPrice(discountedPrice);
                } else {
                    new RegularPricing().calculateTotal(checkout);
                }
            } catch (Exception e) {
                System.err.println("Error applying coupon " + checkout.getCouponCode() + ": " + e.getMessage()); // Use proper logger in real app
                new RegularPricing().calculateTotal(checkout);
            }
        } else {
            new RegularPricing().calculateTotal(checkout);
        }
    }
}