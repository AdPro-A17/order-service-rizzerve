package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;

public interface PricingStrategy {
    void calculateTotal(Checkout checkout);
}