package id.ac.ui.cs.advprog.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.exception.CouponApplicationException;
import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    private ObjectMapper objectMapper;
    private CheckoutRequest validRequest;
    private Checkout mockCheckout;
    private OrderItem mockOrderItem;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController).build();
        objectMapper = new ObjectMapper();

        validRequest = new CheckoutRequest();
        validRequest.setOrderId(UUID.randomUUID());
        validRequest.setCouponCode("DISCOUNT10");

        mockOrderItem = new OrderItem();
        mockOrderItem.setId(UUID.randomUUID());
        mockOrderItem.setMenuItemId(UUID.randomUUID());
        mockOrderItem.setMenuItemName("Nasi Goreng");
        mockOrderItem.setQuantity(2);
        mockOrderItem.setPrice(25000);

        mockCheckout = new Checkout();
        mockCheckout.setId(UUID.randomUUID());
        mockCheckout.setTableNumber(5);
        mockCheckout.setItems(Arrays.asList(mockOrderItem));
        mockCheckout.setTotalPrice(45000);
        mockCheckout.setCouponCode("DISCOUNT10");
        mockCheckout.setDiscountAmount(5000);
    }

    @Test
    void createCheckout_Success() throws Exception {
        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(mockCheckout);

        mockMvc.perform(post("/api/checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutId").value(mockCheckout.getId().toString()))
                .andExpect(jsonPath("$.tableNumber").value(5))
                .andExpect(jsonPath("$.totalPrice").value(45000))
                .andExpect(jsonPath("$.couponCode").value("DISCOUNT10"))
                .andExpect(jsonPath("$.discountAmount").value(5000))
                .andExpect(jsonPath("$.items[0].menuItemName").value("Nasi Goreng"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
    }

    @Test
    void createCheckout_InvalidOrderStatus() throws Exception {
        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
                .thenThrow(new InvalidOrderStatusForCheckoutException("PAID"));

        mockMvc.perform(post("/api/checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Pesanan sudah dibayar"));

        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
    }

    @Test
    void createCheckout_OrderNotFound() throws Exception {
        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
                .thenThrow(new IllegalArgumentException("Order not found"));

        mockMvc.perform(post("/api/checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());

        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
    }

    @Test
    void getCheckoutsByTable_Success() throws Exception {
        List<Checkout> checkouts = Arrays.asList(mockCheckout);
        when(checkoutService.getCheckoutsByTable(5)).thenReturn(checkouts);

        mockMvc.perform(get("/api/checkouts/table/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].checkoutId").value(mockCheckout.getId().toString()))
                .andExpect(jsonPath("$[0].tableNumber").value(5))
                .andExpect(jsonPath("$[0].totalPrice").value(45000));

        verify(checkoutService, times(1)).getCheckoutsByTable(5);
    }

    @Test
    void getCheckoutsByTable_EmptyList() throws Exception {
        when(checkoutService.getCheckoutsByTable(99)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/checkouts/table/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(checkoutService, times(1)).getCheckoutsByTable(99);
    }

    @Test
    void createCheckout_WithoutCoupon() throws Exception {
        CheckoutRequest requestWithoutCoupon = new CheckoutRequest();
        requestWithoutCoupon.setOrderId(UUID.randomUUID());

        Checkout checkoutWithoutCoupon = new Checkout();
        checkoutWithoutCoupon.setId(UUID.randomUUID());
        checkoutWithoutCoupon.setTableNumber(3);
        checkoutWithoutCoupon.setItems(Arrays.asList(mockOrderItem));
        checkoutWithoutCoupon.setTotalPrice(50000);

        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkoutWithoutCoupon);

        mockMvc.perform(post("/api/checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithoutCoupon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(50000))
                .andExpect(jsonPath("$.couponCode").isEmpty())
                .andExpect(jsonPath("$.discountAmount").value(0));

        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
    }

    @Test
    void createCheckout_MultipleItems() throws Exception {
        OrderItem secondItem = new OrderItem();
        secondItem.setId(UUID.randomUUID());
        secondItem.setMenuItemId(UUID.randomUUID());
        secondItem.setMenuItemName("Es Teh");
        secondItem.setQuantity(1);
        secondItem.setPrice(5000);

        Checkout checkoutMultipleItems = new Checkout();
        checkoutMultipleItems.setId(UUID.randomUUID());
        checkoutMultipleItems.setTableNumber(2);
        checkoutMultipleItems.setItems(Arrays.asList(mockOrderItem, secondItem));
        checkoutMultipleItems.setTotalPrice(55000);

        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkoutMultipleItems);

        mockMvc.perform(post("/api/checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].menuItemName").value("Nasi Goreng"))
                .andExpect(jsonPath("$.items[1].menuItemName").value("Es Teh"));

        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
    }
}