package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.pricing.CouponPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.PricingContext;
import id.ac.ui.cs.advprog.orderservice.pricing.RegularPricing;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final OrderRepository orderRepository;
    private final PricingContext pricingContext;
    private final RegularPricing regularPricing;
    private final CouponPricing couponPricing;
    private final RestTemplate restTemplate;

    @Value("${order-service.url}")
    private String orderServiceBaseUrl;

    @Autowired
    public CheckoutService(
            CheckoutRepository checkoutRepository,
            OrderRepository orderRepository,
            PricingContext pricingContext,
            RegularPricing regularPricing,
            CouponPricing couponPricing,
            RestTemplate restTemplate
    ) {
        this.checkoutRepository = checkoutRepository;
        this.orderRepository = orderRepository;
        this.pricingContext = pricingContext;
        this.regularPricing = regularPricing;
        this.couponPricing = couponPricing;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Checkout createCheckout(CheckoutRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!"NEW".equals(order.getStatus())) {
            throw new InvalidOrderStatusForCheckoutException(order.getStatus());
        }

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

        order.confirmOrder();
        order.setTotalPrice(checkout.getTotalPrice());
        orderRepository.save(order);

        checkoutRepository.save(checkout);
        confirmOrderByApi(order.getId());
        return checkout;
    }

    public List<Checkout> getCheckoutsByTable(int tableNumber) {
        return checkoutRepository.findByTableNumber(tableNumber);
    }

    private void confirmOrderByApi(UUID orderId) {
        try {
            String confirmOrderUrl = orderServiceBaseUrl + "/api/orders/" + orderId + "/confirm";
            restTemplate.postForObject(confirmOrderUrl, null, Void.class);
            log.info("Order {} confirmed via API successfully.", orderId);
        } catch (Exception e) {
            log.error("Error confirming order {} via API: {}", orderId, e.getMessage());
        }
    }
}
