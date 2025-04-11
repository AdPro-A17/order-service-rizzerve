package id.ac.ui.cs.advprog.orderservice.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class CheckoutPricingTest {

    @Mock
    private CheckoutPricing checkoutPricing;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkout = new Checkout();
        checkout.setId(UUID.randomUUID());

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Salad");
        item.setQuantity(1);
        item.setPrice(35000.0);
        item.setSubtotal(35000.0);
        orderItems.add(item);

        checkout.setOrderItems(orderItems);
        checkout.setTotalPrice(35000.0);
        doNothing().when(checkoutPricing).calculateFinalPrice(any(Checkout.class));
    }

    @Test
    void testCalculateFinalPriceContract() {
        checkoutPricing.calculateFinalPrice(checkout);

        verify(checkoutPricing).calculateFinalPrice(checkout);
    }

    @Test
    void testCalculateFinalPriceWithNullCheckout() {
        checkoutPricing.calculateFinalPrice(null);

        verify(checkoutPricing).calculateFinalPrice(null);
    }
}