package id.ac.ui.cs.advprog.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CheckoutService checkoutService;

    @Autowired
    private ObjectMapper objectMapper;

    private Checkout checkout;
    private List<OrderItem> items;
    private UUID checkoutId;
    private String tableNumber;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        checkoutId = UUID.randomUUID();
        tableNumber = "A1";

        checkout = new Checkout();
        checkout.setId(checkoutId);
        checkout.setTableNumber(tableNumber);
        checkout.setStatus("SUBMITTED");

        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Nasi Goreng");
        item.setQuantity(2);
        item.setPrice(25000.0);

        items = new ArrayList<>();
        items.add(item);
        checkout.setItems(items);
        checkout.setTotalPrice(50000.0);
        checkout.setFinalPrice(50000.0);

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setOrderId(UUID.randomUUID());
        checkoutRequest.setCouponCode("SAVE10");
    }

    @Test
    void createCheckout_ShouldReturnCreatedCheckout() throws Exception {
        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkout);

        mockMvc.perform(post("/api/checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(checkoutId.toString()))
                .andExpect(jsonPath("$.tableNumber").value(tableNumber))
                .andExpect(jsonPath("$.totalPrice").value(50000.0))
                .andExpect(jsonPath("$.finalPrice").value(50000.0));

        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
    }

    @Test
    void getAllCheckouts_ShouldReturnSubmittedAndProcessingCheckouts() throws Exception {
        List<Checkout> submittedCheckouts = new ArrayList<>();
        submittedCheckouts.add(checkout);

        Checkout processingCheckout = new Checkout();
        processingCheckout.setId(UUID.randomUUID());
        processingCheckout.setTableNumber("B2");
        processingCheckout.setStatus("PROCESSING");
        List<Checkout> processingCheckouts = new ArrayList<>();
        processingCheckouts.add(processingCheckout);

        when(checkoutService.getCheckoutsByStatus(eq("SUBMITTED"))).thenReturn(submittedCheckouts);
        when(checkoutService.getCheckoutsByStatus(eq("PROCESSING"))).thenReturn(processingCheckouts);

        mockMvc.perform(get("/api/checkouts/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.status == 'SUBMITTED')].id").exists())
                .andExpect(jsonPath("$[?(@.status == 'PROCESSING')].id").exists());

        verify(checkoutService, times(1)).getCheckoutsByStatus(eq("SUBMITTED"));
        verify(checkoutService, times(1)).getCheckoutsByStatus(eq("PROCESSING"));
    }

    @Test
    void getCheckoutsByTable_ShouldReturnTableCheckouts() throws Exception {
        List<Checkout> checkouts = new ArrayList<>();
        checkouts.add(checkout);
        when(checkoutService.getCheckoutsByTable(tableNumber)).thenReturn(checkouts);

        mockMvc.perform(get("/api/checkouts/table/{tableNumber}", tableNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(checkoutId.toString()))
                .andExpect(jsonPath("$[0].tableNumber").value(tableNumber));

        verify(checkoutService, times(1)).getCheckoutsByTable(eq(tableNumber));
    }

    @Test
    void updateCheckoutStatus_WithValidIdAndStatus_ShouldReturnUpdatedCheckout() throws Exception {
        String newStatus = "COMPLETED";
        Checkout updatedCheckout = new Checkout();
        updatedCheckout.setId(checkoutId);
        updatedCheckout.setTableNumber(tableNumber);
        updatedCheckout.setStatus(newStatus);
        updatedCheckout.setTotalPrice(50000.0);
        updatedCheckout.setFinalPrice(50000.0);
        updatedCheckout.setItems(items);

        when(checkoutService.updateStatus(eq(checkoutId), eq(newStatus))).thenReturn(updatedCheckout);

        mockMvc.perform(put("/api/checkouts/{checkoutId}/status", checkoutId)
                        .param("status", newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(checkoutId.toString()))
                .andExpect(jsonPath("$.status").value(newStatus))
                .andExpect(jsonPath("$.tableNumber").value(tableNumber));

        verify(checkoutService, times(1)).updateStatus(eq(checkoutId), eq(newStatus));
    }
}