package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.*;
import id.ac.ui.cs.advprog.orderservice.pricing.CouponPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.PricingContext;
import id.ac.ui.cs.advprog.orderservice.pricing.RegularPricing;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

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

        if (!"NEW".equals(order.getStatus())) {
            throw new InvalidOrderStatusForCheckoutException(order.getStatus());
        }

        order.confirmOrder();

        Checkout checkout = new Checkout();
        checkout.setTableNumber(Integer.parseInt(order.getTableNumber()));

        List<OrderItem> itemsCopy = new ArrayList<>(order.getItems());
        checkout.setItems(itemsCopy);
        checkout.setTotalPrice(order.getTotalPrice());

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            pricingContext.setStrategy(couponPricing);
            checkout.setCouponCode(request.getCouponCode());
        } else {
            pricingContext.setStrategy(regularPricing);
        }

        pricingContext.calculateTotal(checkout);

        orderRepository.save(order);
        return checkoutRepository.save(checkout);
    }

    public List<Checkout> getCheckoutsByTable(int tableNumber) {
        return checkoutRepository.findByTableNumber(tableNumber);
    }

    @Transactional
    public Checkout updateStatus(UUID checkoutId, String status) {
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new IllegalArgumentException("Checkout not found"));

        if ("COMPLETED".equals(status)) {
            List<Order> orders = orderRepository.findByTableNumber(String.valueOf(checkout.getTableNumber()));
            if (!orders.isEmpty()) {
                Order order = orders.get(0);
                order.completeOrder();
                orderRepository.save(order);
            }
        }

        return checkoutRepository.save(checkout);
    }
}