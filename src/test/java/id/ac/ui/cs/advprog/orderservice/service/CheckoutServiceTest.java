package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.client.TableServiceClient;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    @Mock
    private TableServiceClient tableServiceClient;

    @InjectMocks
    private CheckoutService checkoutService;

    private Order order;
    private CheckoutRequest checkoutRequest;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(checkoutService, "orderServiceBaseUrl", "http://localhost:8080");

        orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setMenuItemId(UUID.randomUUID());
        orderItem.setMenuItemName("Test Menu");
        orderItem.setQuantity(2);
        orderItem.setPrice(15.0);
        orderItem.setSubtotal(30.0);

        order = new Order();
        order.setId(UUID.randomUUID());
        order.setTableNumber("5");
        order.setStatusString("NEW");
        order.setTotalPrice(30.0);
        order.setItems(List.of(orderItem));

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setOrderId(order.getId());
    }

    @Test
    void createCheckout_WithoutCoupon_Success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setId(UUID.randomUUID());
            return checkout;
        });

        Checkout result = checkoutService.createCheckout(checkoutRequest);

        assertNotNull(result);
        assertEquals(5, result.getTableNumber());
        assertEquals(30.0, result.getTotalPrice());
        assertNull(result.getCouponCode());
        assertEquals(1, result.getItems().size());

        verify(pricingContext).setStrategy(regularPricing);
        verify(pricingContext).calculateTotal(any(Checkout.class));
        verify(orderRepository).save(order);
        verify(checkoutRepository).save(any(Checkout.class));
        verify(restTemplate).postForObject(contains("/api/orders/"), isNull(), eq(Void.class));
        assertEquals("PROCESSING", order.getStatus());
    }

    @Test
    void createCheckout_WithCoupon_Success() {
        checkoutRequest.setCouponCode("DISCOUNT10");
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setId(UUID.randomUUID());
            return checkout;
        });

        Checkout result = checkoutService.createCheckout(checkoutRequest);

        assertNotNull(result);
        assertEquals("DISCOUNT10", result.getCouponCode());

        verify(pricingContext).setStrategy(couponPricing);
        verify(pricingContext).calculateTotal(any(Checkout.class));
    }

    @Test
    void createCheckout_WithEmptyCoupon_UsesRegularPricing() {
        checkoutRequest.setCouponCode("");
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setId(UUID.randomUUID());
            return checkout;
        });

        Checkout result = checkoutService.createCheckout(checkoutRequest);

        assertNotNull(result);
        verify(pricingContext).setStrategy(regularPricing);
    }

    @Test
    void createCheckout_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.createCheckout(checkoutRequest));

        verify(orderRepository, never()).save(any());
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void createCheckout_InvalidOrderStatus_ThrowsException() {
        order.setStatusString("CONFIRMED");
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.createCheckout(checkoutRequest));

        verify(orderRepository, never()).save(any());
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void createCheckout_ApiCallFails_ContinuesExecution() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setId(UUID.randomUUID());
            return checkout;
        });
        when(restTemplate.postForObject(anyString(), any(), eq(Void.class)))
                .thenThrow(new RuntimeException("API call failed"));

        Checkout result = checkoutService.createCheckout(checkoutRequest);

        assertNotNull(result);
        verify(restTemplate).postForObject(anyString(), any(), eq(Void.class));
    }

    @Test
    void getCheckoutsByTable_Success() {
        int tableNumber = 5;
        Checkout checkout1 = new Checkout();
        checkout1.setId(UUID.randomUUID());
        checkout1.setTableNumber(tableNumber);

        Checkout checkout2 = new Checkout();
        checkout2.setId(UUID.randomUUID());
        checkout2.setTableNumber(tableNumber);

        List<Checkout> expectedCheckouts = List.of(checkout1, checkout2);
        when(checkoutRepository.findByTableNumber(tableNumber)).thenReturn(expectedCheckouts);

        List<Checkout> result = checkoutService.getCheckoutsByTable(tableNumber);

        assertEquals(2, result.size());
        assertEquals(expectedCheckouts, result);
        verify(checkoutRepository).findByTableNumber(tableNumber);
    }

    @Test
    void getCheckoutsByTable_EmptyResult() {
        int tableNumber = 99;
        when(checkoutRepository.findByTableNumber(tableNumber)).thenReturn(List.of());

        List<Checkout> result = checkoutService.getCheckoutsByTable(tableNumber);

        assertTrue(result.isEmpty());
        verify(checkoutRepository).findByTableNumber(tableNumber);
    }
}