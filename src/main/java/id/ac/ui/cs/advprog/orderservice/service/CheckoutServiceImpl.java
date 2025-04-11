package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.pricing.CheckoutPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.CouponPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.RegularPricing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final RegularPricing regularPricing;
    private final CouponPricing couponPricing;
    private static final String checkoutNotFoundMessage = "Checkout not found with ID: ";

    @Autowired
    public CheckoutServiceImpl(
            CheckoutRepository checkoutRepository,
            RegularPricing regularPricing,
            CouponPricing couponPricing) {
        this.checkoutRepository = checkoutRepository;
        this.regularPricing = regularPricing;
        this.couponPricing = couponPricing;
    }

    @Override
    public Checkout createCheckout(String tableNumber, List<OrderItem> orderItems) {
        Checkout checkout = new Checkout();
        checkout.setTableNumber(tableNumber);
        checkout.setOrderItems(orderItems);
        double totalPrice = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        checkout.setTotalPrice(totalPrice);
        regularPricing.calculateFinalPrice(checkout);

        return checkoutRepository.save(checkout);
    }

    @Override
    public Checkout applyCoupon(UUID checkoutId, String couponCode) {
        Checkout checkout = checkoutRepository.findById(checkoutId);

        if (checkout == null) {
            throw new IllegalArgumentException(checkoutNotFoundMessage + checkoutId);
        }

        checkout.setCouponCode(couponCode);

        CheckoutPricing strategy;
        if (couponCode != null && !couponCode.isEmpty() && couponPricing.isValidCoupon(couponCode)) {
            strategy = couponPricing;
        } else {
            strategy = regularPricing;
        }

        strategy.calculateFinalPrice(checkout);

        return checkoutRepository.save(checkout);
    }

    @Override
    public Checkout getCheckout(UUID checkoutId) {
        Checkout checkout = checkoutRepository.findById(checkoutId);

        if (checkout == null) {
            throw new IllegalArgumentException(checkoutNotFoundMessage + checkoutId);
        }

        return checkout;
    }
}