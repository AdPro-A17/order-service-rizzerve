package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.orderservice.dto.CouponValidationResponse;
import id.ac.ui.cs.advprog.orderservice.dto.OrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.enums.OrderStatus;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.MenuItemRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private UUID userId;
    private UUID menuItemId1;
    private UUID menuItemId2;
    private MenuItem menuItem1;
    private MenuItem menuItem2;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        menuItemId1 = UUID.randomUUID();
        menuItemId2 = UUID.randomUUID();

        menuItem1 = MenuItem.builder()
                .id(menuItemId1)
                .name("Test Item 1")
                .price(new BigDecimal("10.00"))
                .available(true)
                .build();

        menuItem2 = MenuItem.builder()
                .id(menuItemId2)
                .name("Test Item 2")
                .price(new BigDecimal("15.00"))
                .available(true)
                .build();

        List<OrderItemRequest> itemRequests = Arrays.asList(
                new OrderItemRequest(menuItemId1, 2),
                new OrderItemRequest(menuItemId2, 1)
        );

        checkoutRequest = CheckoutRequest.builder()
                .items(itemRequests)
                .tableNumber("A1")
                .notes("Test notes")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getUserIdFromAuthentication(authentication)).thenReturn(userId);
    }

    @Test
    void testProcessCheckout_Success() {
        when(menuItemRepository.findById(menuItemId1)).thenReturn(Optional.of(menuItem1));
        when(menuItemRepository.findById(menuItemId2)).thenReturn(Optional.of(menuItem2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setId(UUID.randomUUID());
            return checkout;
        });
        CheckoutResponse response = checkoutService.processCheckout(checkoutRequest);

        assertNotNull(response);
        assertEquals("A1", response.getTableNumber());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("35.00"), response.getSubtotal());
        assertEquals(new BigDecimal("35.00"), response.getTotal());
        assertEquals(new BigDecimal("0"), response.getDiscount());
        assertEquals("Test notes", response.getNotes());
        assertEquals(2, response.getItems().size());
        assertTrue(response.isSuccessful());

        verify(orderRepository).save(any(Order.class));
        verify(checkoutRepository).save(any(Checkout.class));
    }

    @Test
    void testProcessCheckout_WithCoupon() {
        when(menuItemRepository.findById(menuItemId1)).thenReturn(Optional.of(menuItem1));
        when(menuItemRepository.findById(menuItemId2)).thenReturn(Optional.of(menuItem2));

        checkoutRequest.setCouponCode("TEST10");

        CouponValidationResponse validationResponse = CouponValidationResponse.builder()
                .valid(true)
                .message("Coupon applied successfully")
                .discountAmount(new BigDecimal("3.50"))
                .build();

        when(couponService.validateCoupon(eq("TEST10"), any(BigDecimal.class)))
                .thenReturn(validationResponse);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(invocation -> {
            Checkout checkout = invocation.getArgument(0);
            checkout.setId(UUID.randomUUID());
            return checkout;
        });

        CheckoutResponse response = checkoutService.processCheckout(checkoutRequest);

        assertNotNull(response);
        assertEquals("A1", response.getTableNumber());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("35.00"), response.getSubtotal());
        assertEquals(new BigDecimal("31.50"), response.getTotal());
        assertEquals(new BigDecimal("3.50"), response.getDiscount());
        assertEquals("TEST10", response.getAppliedCouponCode());

        verify(couponService).validateCoupon(eq("TEST10"), any(BigDecimal.class));
    }

    @Test
    void testProcessCheckout_InvalidCoupon() {
        when(menuItemRepository.findById(menuItemId1)).thenReturn(Optional.of(menuItem1));
        when(menuItemRepository.findById(menuItemId2)).thenReturn(Optional.of(menuItem2));

        checkoutRequest.setCouponCode("INVALID");

        CouponValidationResponse validationResponse = CouponValidationResponse.builder()
                .valid(false)
                .message("Coupon not found")
                .discountAmount(BigDecimal.ZERO)
                .build();

        when(couponService.validateCoupon(eq("INVALID"), any(BigDecimal.class)))
                .thenReturn(validationResponse);

        assertThrows(RuntimeException.class, () -> checkoutService.processCheckout(checkoutRequest));

        verify(couponService).validateCoupon(eq("INVALID"), any(BigDecimal.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testProcessCheckout_MenuItemNotFound() {
        when(menuItemRepository.findById(menuItemId1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> checkoutService.processCheckout(checkoutRequest));

        verify(menuItemRepository).findById(menuItemId1);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testProcessCheckout_MenuItemNotAvailable() {
        MenuItem unavailableItem = MenuItem.builder()
                .id(menuItemId1)
                .name("Unavailable Item")
                .price(new BigDecimal("10.00"))
                .available(false)
                .build();

        when(menuItemRepository.findById(menuItemId1)).thenReturn(Optional.of(unavailableItem));

        assertThrows(RuntimeException.class, () -> checkoutService.processCheckout(checkoutRequest));

        verify(menuItemRepository).findById(menuItemId1);
        verify(orderRepository, never()).save(any(Order.class));
    }
}