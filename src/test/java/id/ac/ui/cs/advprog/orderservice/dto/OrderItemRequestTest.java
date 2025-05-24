package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void whenMenuItemIdIsNull_thenValidationFails() {
        OrderItemRequest request = new OrderItemRequest();
        request.setMenuItemId(null);
        request.setQuantity(1);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenQuantityIsZero_thenValidationFails() {
        OrderItemRequest request = new OrderItemRequest();
        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(0);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenQuantityIsNegative_thenValidationFails() {
        OrderItemRequest request = new OrderItemRequest();
        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(-1);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenAllFieldsAreValid_thenValidationPasses() {
        OrderItemRequest request = new OrderItemRequest();
        request.setMenuItemId(UUID.randomUUID());
        request.setQuantity(1);

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}