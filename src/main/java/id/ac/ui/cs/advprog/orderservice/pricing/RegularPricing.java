package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.stereotype.Component;

@Component
public class RegularPricing implements PricingStrategy {
    @Override
    public void calculateTotal(Checkout checkout) {
        checkout.setDiscountAmount(0);
    }
}