package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.stereotype.Component;

@Component
public class RegularPricing implements PricingStrategy {
    @Override
    public void calculateTotal(Checkout checkout) {
        double total = checkout.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        checkout.setTotalPrice(total);
        checkout.setDiscountAmount(0);
        checkout.setFinalPrice(total);
    }
}