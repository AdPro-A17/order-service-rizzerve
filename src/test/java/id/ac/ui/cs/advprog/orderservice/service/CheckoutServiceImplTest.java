package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.pricing.CouponPricing;
import id.ac.ui.cs.advprog.orderservice.pricing.RegularPricing;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutServiceImplTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private RegularPricing regularPricing;

    @Mock
    private CouponPricing couponPricing;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private Checkout checkout;
    private List<OrderItem> orderItems;
    private UUID checkoutId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        OrderItem item1 = new OrderItem();
        item1.setMenuItemId(UUID.randomUUID());
        item1.setMenuItemName("Nasi Goreng");
        item1.setQuantity(2);
        item1.setPrice(25000.0);
        item1.setSubtotal(50000.0);

        OrderItem item2 = new OrderItem();
        item2.setMenuItemId(UUID.randomUUID());
        item2.setMenuItemName("Es Teh");
        item2.setQuantity(2);
        item2.setPrice(5000.0);
        item2.setSubtotal(10000.0);

        orderItems = Arrays.asList(item1, item2);

        checkout = new Checkout();
        checkoutId = UUID.randomUUID();
        checkout.setId(checkoutId);
        checkout.setTableNumber("A1");
        checkout.setOrderItems(orderItems);
        checkout.setTotalPrice(60000.0);
        checkout.setFinalPrice(60000.0);

        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(i -> i.getArgument(0));
        when(checkoutRepository.findById(checkoutId)).thenReturn(checkout);
    }

    @Test
    void createCheckout_ShouldCreateCheckoutWithRegularPricing() {
        String tableNumber = "A1";
        doAnswer(invocation -> {
            Checkout c = invocation.getArgument(0);
            c.setDiscountAmount(0);
            c.setFinalPrice(c.getTotalPrice());
            return null;
        }).when(regularPricing).calculateFinalPrice(any(Checkout.class));

        Checkout result = checkoutService.createCheckout(tableNumber, orderItems);

        assertNotNull(result);
        assertEquals(tableNumber, result.getTableNumber());
        assertEquals(orderItems, result.getOrderItems());
        assertEquals(60000.0, result.getTotalPrice());
        assertEquals(60000.0, result.getFinalPrice());
        assertEquals(0.0, result.getDiscountAmount());
        verify(regularPricing, times(1)).calculateFinalPrice(any(Checkout.class));
        verify(couponPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test
    void applyCoupon_WithValidCoupon_ShouldApplyCouponPricing() {
        String couponCode = "SAVE10";
        when(couponPricing.isValidCoupon(couponCode)).thenReturn(true);
        doAnswer(invocation -> {
            Checkout c = invocation.getArgument(0);
            c.setDiscountAmount(6000.0);
            c.setFinalPrice(54000.0);
            return null;
        }).when(couponPricing).calculateFinalPrice(any(Checkout.class));

        Checkout result = checkoutService.applyCoupon(checkoutId, couponCode);

        assertNotNull(result);
        assertEquals(couponCode, result.getCouponCode());
        assertEquals(6000.0, result.getDiscountAmount());
        assertEquals(54000.0, result.getFinalPrice());
        verify(couponPricing, times(1)).isValidCoupon(couponCode);
        verify(couponPricing, times(1)).calculateFinalPrice(any(Checkout.class));
        verify(regularPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test
    void applyCoupon_WithInvalidCoupon_ShouldUseRegularPricing() {
        String couponCode = "INVALID";
        when(couponPricing.isValidCoupon(couponCode)).thenReturn(false);
        doAnswer(invocation -> {
            Checkout c = invocation.getArgument(0);
            c.setDiscountAmount(0);
            c.setFinalPrice(c.getTotalPrice());
            return null;
        }).when(regularPricing).calculateFinalPrice(any(Checkout.class));

        Checkout result = checkoutService.applyCoupon(checkoutId, couponCode);

        assertNotNull(result);
        assertEquals(couponCode, result.getCouponCode());
        assertEquals(0.0, result.getDiscountAmount());
        assertEquals(60000.0, result.getFinalPrice());
        verify(couponPricing, times(1)).isValidCoupon(couponCode);
        verify(regularPricing, times(1)).calculateFinalPrice(any(Checkout.class));
        verify(couponPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test
    void applyCoupon_WithNullCoupon_ShouldUseRegularPricing() {
        String couponCode = null;
        doAnswer(invocation -> {
            Checkout c = invocation.getArgument(0);
            c.setDiscountAmount(0);
            c.setFinalPrice(c.getTotalPrice());
            return null;
        }).when(regularPricing).calculateFinalPrice(any(Checkout.class));

        Checkout result = checkoutService.applyCoupon(checkoutId, couponCode);

        assertNotNull(result);
        assertNull(result.getCouponCode());
        assertEquals(0.0, result.getDiscountAmount());
        assertEquals(60000.0, result.getFinalPrice());
        verify(couponPricing, never()).isValidCoupon(anyString());
        verify(regularPricing, times(1)).calculateFinalPrice(any(Checkout.class));
        verify(couponPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test
    void applyCoupon_WithEmptyCoupon_ShouldUseRegularPricing() {
        String couponCode = "";
        doAnswer(invocation -> {
            Checkout c = invocation.getArgument(0);
            c.setDiscountAmount(0);
            c.setFinalPrice(c.getTotalPrice());
            return null;
        }).when(regularPricing).calculateFinalPrice(any(Checkout.class));

        Checkout result = checkoutService.applyCoupon(checkoutId, couponCode);

        assertNotNull(result);
        assertEquals("", result.getCouponCode());
        assertEquals(0.0, result.getDiscountAmount());
        assertEquals(60000.0, result.getFinalPrice());
        verify(couponPricing, never()).isValidCoupon(anyString());
        verify(regularPricing, times(1)).calculateFinalPrice(any(Checkout.class));
        verify(couponPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test
    void applyCoupon_WithNonExistentCheckoutId_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(checkoutRepository.findById(nonExistentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.applyCoupon(nonExistentId, "SAVE10");
        });
        assertEquals("Checkout not found with ID: " + nonExistentId, exception.getMessage());
        verify(checkoutRepository, times(1)).findById(nonExistentId);
        verify(couponPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(regularPricing, never()).calculateFinalPrice(any(Checkout.class));
        verify(checkoutRepository, never()).save(any(Checkout.class));
    }

    @Test
    void getCheckout_WithValidId_ShouldReturnCheckout() {
        Checkout result = checkoutService.getCheckout(checkoutId);

        assertNotNull(result);
        assertEquals(checkoutId, result.getId());
        verify(checkoutRepository, times(1)).findById(checkoutId);
    }

    @Test
    void getCheckout_WithNonExistentId_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(checkoutRepository.findById(nonExistentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.getCheckout(nonExistentId);
        });
        assertEquals("Checkout not found with ID: " + nonExistentId, exception.getMessage());
        verify(checkoutRepository, times(1)).findById(nonExistentId);
    }
}