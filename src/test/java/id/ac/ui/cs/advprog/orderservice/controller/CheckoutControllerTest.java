package id.ac.ui.cs.advprog.orderservice.controller;

/*
 * TEMPORARILY COMMENTED OUT TO PREVENT CONFLICTS WITH HUSIN'S ORDER IMPLEMENTATION
 */

/*
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController).build();
    }

    @Test
    void testCheckout() throws Exception {
        // Setup mock response
        CheckoutResponse mockResponse = CheckoutResponse.builder()
                .orderId(UUID.randomUUID())
                .checkoutId(UUID.randomUUID())
                .tableNumber("T1")
                .status("PENDING")
                .subtotal(BigDecimal.valueOf(100))
                .total(BigDecimal.valueOf(100))
                .successful(true)
                .build();

        // Mock service
        when(checkoutService.processCheckout(any(CheckoutRequest.class))).thenReturn(mockResponse);

        // Perform request
        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableNumber\":\"T1\",\"items\":[{\"menuItemId\":\"" + UUID.randomUUID() + "\",\"quantity\":2}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.checkoutId").exists())
                .andExpect(jsonPath("$.tableNumber").value("T1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.subtotal").value(100))
                .andExpect(jsonPath("$.total").value(100))
                .andExpect(jsonPath("$.successful").value(true));
    }
}
*/