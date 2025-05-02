package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.orderservice.dto.OrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkout_ShouldReturnCheckoutResponse() {
        CheckoutRequest request = new CheckoutRequest();
        request.setTableNumber("A1");
        request.setItems(List.of(new OrderItemRequest(UUID.randomUUID(), 2)));

        CheckoutResponse mockResponse = CheckoutResponse.builder()
                .orderId(UUID.randomUUID())
                .checkoutId(UUID.randomUUID())
                .tableNumber("A1")
                .orderTime(LocalDateTime.now())
                .total(new BigDecimal("50000"))
                .successful(true)
                .build();

        when(checkoutService.processCheckout(any(CheckoutRequest.class))).thenReturn(mockResponse);
        ResponseEntity<CheckoutResponse> responseEntity = checkoutController.checkout(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(mockResponse.getOrderId(), responseEntity.getBody().getOrderId());
        assertEquals(mockResponse.getTableNumber(), responseEntity.getBody().getTableNumber());
    }

    @Test
    void testCheckout_ShouldReturnSuccessMessage() {
        ResponseEntity<String> responseEntity = checkoutController.testCheckout();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Checkout endpoint is working!", responseEntity.getBody());
    }
}