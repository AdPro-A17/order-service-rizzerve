package id.ac.ui.cs.advprog.orderservice.pricing;

import org.springframework.stereotype.Component;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import lombok.Setter;

@Component
@Setter
public class PricingContext {
    private PricingStrategy strategy;

    public void calculateTotal(Checkout checkout) {
        if (strategy != null) {
            strategy.calculateTotal(checkout);
        }
    }
}