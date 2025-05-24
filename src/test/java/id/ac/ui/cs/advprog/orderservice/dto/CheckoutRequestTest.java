package id.ac.ui.cs.advprog.orderservice.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void whenTableNumberIsNull_thenValidationFails() {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Collections.singletonList(new OrderItemRequest(UUID.randomUUID(), 1)));
        request.setTableNumber(null);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenTableNumberIsEmpty_thenValidationFails() {
        CheckoutRequest request = new CheckoutRequest();
        request.setItems(Collections.singletonList(new OrderItemRequest(UUID.randomUUID(), 1)));
        request.setTableNumber("");

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenItemsAreNull_thenValidationFails() {
        CheckoutRequest request = new CheckoutRequest();
        request.setTableNumber("A1");
        request.setItems(null);

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void whenAllFieldsAreValid_thenValidationPasses() {
        CheckoutRequest request = new CheckoutRequest();
        request.setTableNumber("A1");
        request.setItems(Collections.singletonList(new OrderItemRequest(UUID.randomUUID(), 1)));
        request.setCouponCode("WELCOME10");
        request.setNotes("Test notes");

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}