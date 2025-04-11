package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;

import java.util.List;
import java.util.UUID;

interface CheckoutService {
    Checkout createCheckout(String tableNumber, List<OrderItem> orderItems);
    Checkout applyCoupon(UUID checkoutId, String couponCode);
    Checkout getCheckout(UUID checkoutId);
}
