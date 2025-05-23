package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.stereotype.Service;
import lombok.Setter;

@Service
@Setter
public class PricingContext {
    private PricingStrategy strategy;

    public void calculateTotal(Checkout checkout) {
        strategy.calculateTotal(checkout);
    }
}