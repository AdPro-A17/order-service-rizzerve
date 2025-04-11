package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.stereotype.Component;

@Component
public class RegularPricing implements CheckoutPricing {
    @Override
    public void calculateFinalPrice(Checkout checkout) {
        checkout.setDiscountAmount(0);
        checkout.setFinalPrice(checkout.getTotalPrice());
    }
}