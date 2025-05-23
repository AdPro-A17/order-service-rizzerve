package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.pricing.PricingContext;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PricingContext pricingContext;

    @InjectMocks
    private CheckoutService checkoutService;

    private Checkout checkout;
    private Order order;
    private List<OrderItem> items;
    private UUID checkoutId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkoutId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        items = new ArrayList<>();

        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Pasta");
        item.setQuantity(2);
        item.setPrice(75000.0);
        item.setSubtotal(150000.0);
        items.add(item);

        order = new Order("A7");
        order.setId(orderId);
        order.setItems(items);

        checkout = new Checkout();
        checkout.setId(checkoutId);
        checkout.setItems(items);
        checkout.setTableNumber("A7");
        checkout.setTotalPrice(150000.0);
        checkout.setFinalPrice(150000.0);
    }

    @Test
    void testCreateCheckout() {
        CheckoutRequest request = new CheckoutRequest();
        request.setOrderId(orderId);
        request.setCouponCode("SAVE10");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(checkoutRepository.save(any(Checkout.class))).thenReturn(checkout);
        doNothing().when(pricingContext).calculateTotal(any(Checkout.class));

        Checkout result = checkoutService.createCheckout(request);

        assertNotNull(result);
        assertEquals("A7", result.getTableNumber());
        verify(orderRepository).findById(orderId);
        verify(checkoutRepository).save(any(Checkout.class));
        verify(pricingContext).calculateTotal(any(Checkout.class));
    }

    @Test
    void testGetCheckoutsByTable() {
        List<Checkout> checkouts = List.of(checkout);
        when(checkoutRepository.findByTableNumber("A7")).thenReturn(checkouts);

        List<Checkout> result = checkoutService.getCheckoutsByTable("A7");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A7", result.getFirst().getTableNumber());
        verify(checkoutRepository).findByTableNumber("A7");
    }

    @Test
    void testGetCheckoutsByStatus() {
        List<Checkout> checkouts = List.of(checkout);
        when(checkoutRepository.findByStatus("SUBMITTED")).thenReturn(checkouts);

        List<Checkout> result = checkoutService.getCheckoutsByStatus("SUBMITTED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUBMITTED", result.getFirst().getStatus());
        verify(checkoutRepository).findByStatus("SUBMITTED");
    }

    @Test
    void testUpdateStatus() {
        when(checkoutRepository.findById(checkoutId)).thenReturn(Optional.of(checkout));
        when(checkoutRepository.save(any(Checkout.class))).thenReturn(checkout);

        Checkout result = checkoutService.updateStatus(checkoutId, "COMPLETED");

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(checkoutRepository).findById(checkoutId);
        verify(checkoutRepository).save(any(Checkout.class));
    }

    @Test
    void testUpdateStatus_CheckoutNotFound() {
        when(checkoutRepository.findById(checkoutId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                checkoutService.updateStatus(checkoutId, "COMPLETED"));

        verify(checkoutRepository).findById(checkoutId);
        verify(checkoutRepository, never()).save(any(Checkout.class));
    }
}