package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

class PricingContextTest {

    @Mock
    private PricingStrategy pricingStrategy;

    private PricingContext pricingContext;
    private Checkout checkout;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pricingContext = new PricingContext();
        checkout = new Checkout();
        items = new ArrayList<>();

        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Burger");
        item.setQuantity(2);
        item.setPrice(50000.0);
        items.add(item);

        checkout.setItems(items);
    }

    @Test
    void testCalculateTotal() {
        pricingContext.setStrategy(pricingStrategy);
        doNothing().when(pricingStrategy).calculateTotal(any(Checkout.class));

        pricingContext.calculateTotal(checkout);

        verify(pricingStrategy).calculateTotal(checkout);
    }

    @Test
    void testCalculateTotalWithNullStrategy() {
        assertThrows(NullPointerException.class, () ->
                pricingContext.calculateTotal(checkout));
    }
}