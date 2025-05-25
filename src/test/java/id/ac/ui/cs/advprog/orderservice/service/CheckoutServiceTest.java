package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.exception.CouponApplicationException;
import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.pricing.CouponPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.PricingContext;
import id.ac.ui.cs.advprog.orderservice.pricing.RegularPricing;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PricingContext pricingContext;

    @Mock
    private RegularPricing regularPricing;

    @Mock
    private CouponPricing couponPricing;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CheckoutService checkoutService;

    private Order order;
    private CheckoutRequest checkoutRequest;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        order = new Order("T1");
        order.setId(orderId);
        order.confirmOrder(); // Set to PROCESSING state

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setOrderId(orderId);
    }

    @Test
    void testProcessCheckout_RegularPricing() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(pricingContext.getStrategy(any())).thenReturn(regularPricing);
        when(regularPricing.calculateTotal(any())).thenReturn(100.0);
        when(checkoutRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Checkout checkout = checkoutService.processCheckout(checkoutRequest);

        assertNotNull(checkout);
        assertEquals(orderId, checkout.getOrderId());
        assertEquals(100.0, checkout.getTotalAmount());
        verify(checkoutRepository).save(any());
    }

    @Test
    void testProcessCheckout_WithCoupon() {
        checkoutRequest.setCouponCode("DISCOUNT10");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(pricingContext.getStrategy(any())).thenReturn(couponPricing);
        when(couponPricing.calculateTotal(any())).thenReturn(90.0);
        when(checkoutRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Checkout checkout = checkoutService.processCheckout(checkoutRequest);

        assertNotNull(checkout);
        assertEquals(orderId, checkout.getOrderId());
        assertEquals(90.0, checkout.getTotalAmount());
        assertEquals("DISCOUNT10", checkout.getCouponCode());
        verify(checkoutRepository).save(any());
    }

    @Test
    void testProcessCheckout_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> checkoutService.processCheckout(checkoutRequest));
    }

    @Test
    void testProcessCheckout_InvalidOrderStatus() {
        Order newOrder = new Order("T1"); // NEW state
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(newOrder));

        assertThrows(InvalidOrderStatusForCheckoutException.class,
                () -> checkoutService.processCheckout(checkoutRequest));
    }

    @Test
    void testProcessCheckout_CouponApplicationError() {
        checkoutRequest.setCouponCode("INVALID");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(pricingContext.getStrategy(any())).thenReturn(couponPricing);
        when(couponPricing.calculateTotal(any())).thenThrow(new CouponApplicationException("Invalid coupon"));

        assertThrows(CouponApplicationException.class,
                () -> checkoutService.processCheckout(checkoutRequest));
    }
}