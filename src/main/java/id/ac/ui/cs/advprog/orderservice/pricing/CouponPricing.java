package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CouponPricing implements PricingStrategy {

    private final RestTemplate restTemplate;
    private final String couponServiceUrl;

    @Autowired
    public CouponPricing(RestTemplate restTemplate, @Value("${coupon-service.url}") String couponServiceUrl) {
        this.restTemplate = restTemplate;
        this.couponServiceUrl = couponServiceUrl;
    }

    @Override
    public void calculateTotal(Checkout checkout) {
        try {
            String url = couponServiceUrl + "/coupon/" + checkout.getCouponCode() + "/apply?total=" + checkout.getTotalPrice();
            Double finalPrice = restTemplate.postForObject(url, null, Double.class);

            if (finalPrice != null) {
                checkout.setDiscountAmount(checkout.getTotalPrice() - finalPrice);
                checkout.setFinalPrice(finalPrice);
            }
        } catch (Exception e) {
            new RegularPricing().calculateTotal(checkout);
        }
    }
}
