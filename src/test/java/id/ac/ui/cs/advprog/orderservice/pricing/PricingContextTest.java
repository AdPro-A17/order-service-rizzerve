package id.ac.ui.cs.advprog.orderservice.pricing;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingContextTest {

    @Mock
    private PricingStrategy mockStrategy;

    @InjectMocks
    private PricingContext pricingContext;

    @Test
    void calculateTotal_WithStrategy_CallsStrategyCalculateTotal() {
        Checkout checkout = new Checkout();
        pricingContext.setStrategy(mockStrategy);

        pricingContext.calculateTotal(checkout);

        verify(mockStrategy).calculateTotal(checkout);
    }

    @Test
    void calculateTotal_WithNullStrategy_DoesNothing() {
        Checkout checkout = new Checkout();
        pricingContext.setStrategy(null);

        pricingContext.calculateTotal(checkout); // Should not throw exception

        verifyNoInteractions(mockStrategy);
    }
}