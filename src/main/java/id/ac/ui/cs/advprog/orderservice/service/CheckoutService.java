package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.pricing.CouponPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.PricingContext;
import id.ac.ui.cs.advprog.orderservice.pricing.RegularPricing;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final OrderRepository orderRepository;
    private final PricingContext pricingContext;
    private final RegularPricing regularPricing;
    private final CouponPricing couponPricing;

    @Autowired
    public CheckoutService(CheckoutRepository checkoutRepository,
                           OrderRepository orderRepository,
                           PricingContext pricingContext,
                           RegularPricing regularPricing,
                           CouponPricing couponPricing) {
        this.checkoutRepository = checkoutRepository;
        this.orderRepository = orderRepository;
        this.pricingContext = pricingContext;
        this.regularPricing = regularPricing;
        this.couponPricing = couponPricing;
    }

    @Transactional
    public Checkout createCheckout(CheckoutRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Checkout checkout = new Checkout();
        checkout.setTableNumber(order.getTableNumber());
        checkout.setItems(order.getItems());

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            checkout.setCouponCode(request.getCouponCode());
            pricingContext.setStrategy(couponPricing);
        } else {
            pricingContext.setStrategy(regularPricing);
        }

        pricingContext.calculateTotal(checkout);

        order.getItems().clear();
        orderRepository.save(order);

        return checkoutRepository.save(checkout);
    }

    public List<Checkout> getCheckoutsByTable(String tableNumber) {
        return checkoutRepository.findByTableNumber(tableNumber);
    }

    public List<Checkout> getCheckoutsByStatus(String status) {
        return checkoutRepository.findByStatus(status);
    }

    @Transactional
    public Checkout updateStatus(UUID checkoutId, String status) {
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new IllegalArgumentException("Checkout not found"));

        checkout.setStatus(status);
        checkout.setUpdatedAt(LocalDateTime.now());
        return checkoutRepository.save(checkout);
    }
}