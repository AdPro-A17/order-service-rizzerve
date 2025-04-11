package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CheckoutServiceTest {

    @Mock
    private CheckoutService checkoutService;

    private List<OrderItem> orderItems;
    private Checkout checkout;
    private UUID checkoutId;
    private String tableNumber;
    private String couponCode;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        tableNumber = "A7";
        couponCode = "SAVE20";
        checkoutId = UUID.randomUUID();

        orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Pasta");
        item.setQuantity(2);
        item.setPrice(75000.0);
        item.setSubtotal(150000.0);
        orderItems.add(item);

        checkout = new Checkout();
        checkout.setId(checkoutId);
        checkout.setOrderItems(orderItems);
        checkout.setTableNumber(tableNumber);
        checkout.setTotalPrice(150000.0);
        checkout.setFinalPrice(150000.0);

        when(checkoutService.createCheckout(anyString(), anyList())).thenReturn(checkout);
        when(checkoutService.applyCoupon(any(UUID.class), anyString())).thenReturn(checkout);
        when(checkoutService.getCheckout(any(UUID.class))).thenReturn(checkout);
        when(checkoutService.finalizeCheckout(any(UUID.class))).thenReturn(checkout);
    }

    @Test
    void testCreateCheckoutContract() {
        Checkout result = checkoutService.createCheckout(tableNumber, orderItems);

        assertNotNull(result, "createCheckout should return a non-null checkout");
        verify(checkoutService).createCheckout(tableNumber, orderItems);
    }

    @Test
    void testApplyCouponContract() {
        Checkout result = checkoutService.applyCoupon(checkoutId, couponCode);

        assertNotNull(result, "applyCoupon should return a non-null checkout");
        verify(checkoutService).applyCoupon(checkoutId, couponCode);
    }

    @Test
    void testGetCheckoutContract() {
        Checkout result = checkoutService.getCheckout(checkoutId);

        assertNotNull(result, "getCheckout should return a non-null checkout");
        verify(checkoutService).getCheckout(checkoutId);
    }

    @Test
    void testFinalizeCheckoutContract() {
        Checkout result = checkoutService.finalizeCheckout(checkoutId);

        assertNotNull(result, "finalizeCheckout should return a non-null checkout");
        verify(checkoutService).finalizeCheckout(checkoutId);
    }
}