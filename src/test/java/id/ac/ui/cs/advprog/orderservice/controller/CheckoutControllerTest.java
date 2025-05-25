//package id.ac.ui.cs.advprog.orderservice.controller;
//
//import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
//import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;
//import id.ac.ui.cs.advprog.orderservice.exception.CouponApplicationException;
//import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
//import id.ac.ui.cs.advprog.orderservice.model.Checkout;
//import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
//import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CheckoutController.class)
//class CheckoutControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CheckoutService checkoutService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private CheckoutRequest checkoutRequest;
//    private Checkout checkout;
//    private OrderItem orderItem1;
//    private OrderItem orderItem2;
//
//    @BeforeEach
//    void setUp() {
//        // Setup OrderItems
//        orderItem1 = new OrderItem();
//        orderItem1.setId(UUID.randomUUID());
//        orderItem1.setMenuItemId(UUID.randomUUID());
//        orderItem1.setMenuItemName("Pizza");
//        orderItem1.setQuantity(2);
//        orderItem1.setPrice(15.99);
//        orderItem1.setSubtotal(31.98);
//
//        orderItem2 = new OrderItem();
//        orderItem2.setId(UUID.randomUUID());
//        orderItem2.setMenuItemId(UUID.randomUUID());
//        orderItem2.setMenuItemName("Burger");
//        orderItem2.setQuantity(1);
//        orderItem2.setPrice(12.50);
//        orderItem2.setSubtotal(12.50);
//
//        // Setup CheckoutRequest
//        checkoutRequest = new CheckoutRequest();
//        checkoutRequest.setOrderId(UUID.randomUUID());
//        checkoutRequest.setCouponCode("SAVE10");
//
//        // Setup Checkout
//        checkout = new Checkout();
//        checkout.setId(UUID.randomUUID());
//        checkout.setTableNumber(1);
//        checkout.setItems(Arrays.asList(orderItem1, orderItem2));
//        checkout.setTotalPrice(39.48);
//        checkout.setCouponCode("SAVE10");
//        checkout.setDiscountAmount(5.00);
//    }
//
//    @Test
//    void testCreateCheckout_WhenValidRequest_ShouldReturnCheckoutResponse() throws Exception {
//        // Given
//        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkout);
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.checkoutId").value(checkout.getId().toString()))
//                .andExpect(jsonPath("$.tableNumber").value(1))
//                .andExpect(jsonPath("$.totalPrice").value(39.48))
//                .andExpect(jsonPath("$.couponCode").value("SAVE10"))
//                .andExpect(jsonPath("$.discountAmount").value(5.00))
//                .andExpect(jsonPath("$.items").isArray())
//                .andExpect(jsonPath("$.items[0].menuItemName").value("Pizza"))
//                .andExpect(jsonPath("$.items[0].quantity").value(2))
//                .andExpect(jsonPath("$.items[0].price").value(15.99))
//                .andExpect(jsonPath("$.items[0].subtotal").value(31.98))
//                .andExpect(jsonPath("$.items[1].menuItemName").value("Burger"))
//                .andExpect(jsonPath("$.items[1].quantity").value(1))
//                .andExpect(jsonPath("$.items[1].price").value(12.50))
//                .andExpect(jsonPath("$.items[1].subtotal").value(12.50));
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenValidRequestWithoutCoupon_ShouldReturnCheckoutResponse() throws Exception {
//        // Given
//        checkoutRequest.setCouponCode(null);
//        checkout.setCouponCode(null);
//        checkout.setDiscountAmount(0.0);
//        checkout.setTotalPrice(44.48);
//
//        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkout);
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.checkoutId").value(checkout.getId().toString()))
//                .andExpect(jsonPath("$.tableNumber").value(1))
//                .andExpect(jsonPath("$.totalPrice").value(44.48))
//                .andExpect(jsonPath("$.couponCode").isEmpty())
//                .andExpect(jsonPath("$.discountAmount").value(0.0));
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenInvalidOrderStatusException_ShouldReturnBadRequest() throws Exception {
//        // Given
//        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
//                .thenThrow(new InvalidOrderStatusForCheckoutException("CONFIRMED"));
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpected(content().string("Pesanan sudah dibayar"));
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
//        // Given
//        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
//                .thenThrow(new IllegalArgumentException("Order not found"));
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isNotFound());
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenCouponApplicationException_ShouldReturnBadRequest() throws Exception {
//        // Given
//        String errorMessage = "Invalid coupon code";
//        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
//                .thenThrow(new CouponApplicationException(errorMessage));
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(errorMessage));
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenInvalidJson_ShouldReturnBadRequest() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("invalid json"))
//                .andExpect(status().isBadRequest());
//
//        verify(checkoutService, never()).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testGetCheckoutsByTable_WhenTableHasCheckouts_ShouldReturnCheckoutsList() throws Exception {
//        // Given
//        int tableNumber = 1;
//        Checkout checkout2 = new Checkout();
//        checkout2.setId(UUID.randomUUID());
//        checkout2.setTableNumber(tableNumber);
//        checkout2.setItems(Arrays.asList(orderItem1));
//        checkout2.setTotalPrice(31.98);
//        checkout2.setCouponCode(null);
//        checkout2.setDiscountAmount(0.0);
//
//        List<Checkout> checkouts = Arrays.asList(checkout, checkout2);
//        when(checkoutService.getCheckoutsByTable(tableNumber)).thenReturn(checkouts);
//
//        // When & Then
//        mockMvc.perform(get("/api/checkouts/table/{tableNumber}", tableNumber))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].checkoutId").value(checkout.getId().toString()))
//                .andExpect(jsonPath("$[0].tableNumber").value(tableNumber))
//                .andExpect(jsonPath("$[0].totalPrice").value(39.48))
//                .andExpect(jsonPath("$[0].couponCode").value("SAVE10"))
//                .andExpect(jsonPath("$[0].discountAmount").value(5.00))
//                .andExpect(jsonPath("$[0].items").isArray())
//                .andExpect(jsonPath("$[0].items.length()").value(2))
//                .andExpected(jsonPath("$[1].checkoutId").value(checkout2.getId().toString()))
//                .andExpect(jsonPath("$[1].tableNumber").value(tableNumber))
//                .andExpect(jsonPath("$[1].totalPrice").value(31.98))
//                .andExpect(jsonPath("$[1].couponCode").isEmpty())
//                .andExpect(jsonPath("$[1].discountAmount").value(0.0))
//                .andExpect(jsonPath("$[1].items.length()").value(1));
//
//        verify(checkoutService, times(1)).getCheckoutsByTable(tableNumber);
//    }
//
//    @Test
//    void testGetCheckoutsByTable_WhenTableHasNoCheckouts_ShouldReturnEmptyList() throws Exception {
//        // Given
//        int tableNumber = 999;
//        when(checkoutService.getCheckoutsByTable(tableNumber)).thenReturn(Arrays.asList());
//
//        // When & Then
//        mockMvc.perform(get("/api/checkouts/table/{tableNumber}", tableNumber))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//
//        verify(checkoutService, times(1)).getCheckoutsByTable(tableNumber);
//    }
//
//    @Test
//    void testGetCheckoutsByTable_WhenTableNumberIsZero_ShouldReturnEmptyList() throws Exception {
//        // Given
//        int tableNumber = 0;
//        when(checkoutService.getCheckoutsByTable(tableNumber)).thenReturn(Arrays.asList());
//
//        // When & Then
//        mockMvc.perform(get("/api/checkouts/table/{tableNumber}", tableNumber))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//
//        verify(checkoutService, times(1)).getCheckoutsByTable(tableNumber);
//    }
//
//    @Test
//    void testGetCheckoutsByTable_WhenTableNumberIsNegative_ShouldReturnEmptyList() throws Exception {
//        // Given
//        int tableNumber = -1;
//        when(checkoutService.getCheckoutsByTable(tableNumber)).thenReturn(Arrays.asList());
//
//        // When & Then
//        mockMvc.perform(get("/api/checkouts/table/{tableNumber}", tableNumber))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpected(jsonPath("$.length()").value(0));
//
//        verify(checkoutService, times(1)).getCheckoutsByTable(tableNumber);
//    }
//
//    @Test
//    void testCreateCheckout_WhenRequestHasEmptyOrderId_ShouldCallService() throws Exception {
//        // Given
//        checkoutRequest.setOrderId(null);
//        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
//                .thenThrow(new IllegalArgumentException("Order not found"));
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isNotFound());
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenRequestHasEmptyCouponCode_ShouldReturnSuccessWithoutCoupon() throws Exception {
//        // Given
//        checkoutRequest.setCouponCode("");
//        checkout.setCouponCode("");
//        checkout.setDiscountAmount(0.0);
//        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkout);
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.couponCode").value(""))
//                .andExpect(jsonPath("$.discountAmount").value(0.0));
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testCreateCheckout_WhenServiceThrowsGenericException_ShouldReturnInternalServerError() throws Exception {
//        // Given
//        when(checkoutService.createCheckout(any(CheckoutRequest.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isInternalServerError());
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//
//    @Test
//    void testGetCheckoutsByTable_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
//        // Given
//        int tableNumber = 1;
//        when(checkoutService.getCheckoutsByTable(tableNumber))
//                .thenThrow(new RuntimeException("Database error"));
//
//        // When & Then
//        mockMvc.perform(get("/api/checkouts/table/{tableNumber}", tableNumber))
//                .andExpect(status().isInternalServerError());
//
//        verify(checkoutService, times(1)).getCheckoutsByTable(tableNumber);
//    }
//
//    @Test
//    void testCreateCheckout_WhenCheckoutHasMultipleItems_ShouldMapAllItemsCorrectly() throws Exception {
//        // Given
//        OrderItem orderItem3 = new OrderItem();
//        orderItem3.setId(UUID.randomUUID());
//        orderItem3.setMenuItemId(UUID.randomUUID());
//        orderItem3.setMenuItemName("Pasta");
//        orderItem3.setQuantity(3);
//        orderItem3.setPrice(18.00);
//        orderItem3.setSubtotal(54.00);
//
//        checkout.getItems().add(orderItem3);
//        checkout.setTotalPrice(93.48);
//
//        when(checkoutService.createCheckout(any(CheckoutRequest.class))).thenReturn(checkout);
//
//        // When & Then
//        mockMvc.perform(post("/api/checkouts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkoutRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.items.length()").value(3))
//                .andExpect(jsonPath("$.items[2].menuItemName").value("Pasta"))
//                .andExpect(jsonPath("$.items[2].quantity").value(3))
//                .andExpect(jsonPath("$.items[2].price").value(18.00))
//                .andExpect(jsonPath("$.items[2].subtotal").value(54.00))
//                .andExpect(jsonPath("$.totalPrice").value(93.48));
//
//        verify(checkoutService, times(1)).createCheckout(any(CheckoutRequest.class));
//    }
//}