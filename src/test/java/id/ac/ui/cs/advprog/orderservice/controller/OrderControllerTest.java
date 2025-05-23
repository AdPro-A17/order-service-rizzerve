package id.ac.ui.cs.advprog.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.orderservice.config.SecurityConfig;
import id.ac.ui.cs.advprog.orderservice.dto.AddOrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.orderservice.dto.UpdateQuantityRequest;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.model.state.NewOrderState;
import id.ac.ui.cs.advprog.orderservice.security.JwtAuthFilter;
import id.ac.ui.cs.advprog.orderservice.security.JwtService;
import id.ac.ui.cs.advprog.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = OrderController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private UUID orderId;
    private UUID itemId;
    private Order testOrder;
    private OrderItem testItem;
    private User adminUser;
    private CreateOrderRequest validCreateRequest;
    private AddOrderItemRequest validAddItemRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        orderId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        testOrder = new Order("Table 1");
        testOrder.setId(orderId);
        testOrder.setState(new NewOrderState(testOrder));
        
        testItem = new OrderItem();
        testItem.setId(itemId);
        testItem.setMenuItemId(UUID.randomUUID());
        testItem.setMenuItemName("Sushi Roll");
        testItem.setPrice(12.99);
        testItem.setQuantity(2);
        
        testOrder.addItem(testItem);

        adminUser = new User(
            "admin", 
            "password", 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);

        validCreateRequest = new CreateOrderRequest();
        validCreateRequest.setTableNumber("Table 1");

        validAddItemRequest = new AddOrderItemRequest();
        validAddItemRequest.setMenuItemId(UUID.randomUUID());
        validAddItemRequest.setMenuItemName("California Roll");
        validAddItemRequest.setPrice(10.99);
        validAddItemRequest.setQuantity(1);
    }

    // PUBLIC ACCESS TESTS - No Authentication Required

    @Test
    @WithAnonymousUser
    void testCreateOrder_noAuthRequired() throws Exception {
        when(orderService.createOrder(anyString())).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.tableNumber", is("Table 1")))
                .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    @WithAnonymousUser
    void testGetOrderById_noAuthRequired() throws Exception {
        when(orderService.findOrderById(eq(orderId))).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.tableNumber", is("Table 1")))
                .andExpect(jsonPath("$.status", is("NEW")))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @WithAnonymousUser
    void testAddItemToOrder_noAuthRequired() throws Exception {
        when(orderService.addItemToOrder(
                eq(orderId), 
                any(UUID.class), 
                anyString(), 
                any(Double.class), 
                anyInt())).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    @WithAnonymousUser
    void testUpdateItemQuantity_noAuthRequired() throws Exception {
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setQuantity(3);
        
        when(orderService.updateItemQuantity(orderId, itemId, 3)).thenReturn(testOrder);

        mockMvc.perform(put("/api/orders/{orderId}/items/{itemId}", orderId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    // CUSTOMER ACCESS TESTS - Customers can checkout without authentication

    @Test
    @WithAnonymousUser
    void testConfirmOrder_noAuthRequired() throws Exception {
        when(orderService.confirmOrder(orderId)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    // AUTHENTICATION FAILURE TESTS - Only admin endpoints should require auth

    @Test
    @WithAnonymousUser
    void testGetAllOrders_unauthenticatedShouldFail() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testRemoveItemFromOrder_unauthenticatedShouldFail() throws Exception {
        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testCompleteOrder_unauthenticatedShouldFail() throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/complete", orderId))
                .andExpect(status().isForbidden());
    }

    // AUTHORIZATION FAILURE TESTS - Only admin functions should be restricted

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllOrders_authenticatedNonAdminShouldFail() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRemoveItemFromOrder_authenticatedNonAdminShouldFail() throws Exception {
        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCompleteOrder_authenticatedNonAdminShouldFail() throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/complete", orderId))
                .andExpect(status().isForbidden());
    }

    // ADMIN AUTHENTICATION TESTS - Success cases

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllOrders_authenticatedAdminShouldSucceed() throws Exception {
        Order order2 = new Order("Table 2");
        order2.setId(UUID.randomUUID());
        
        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderService.findAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(orderId.toString())))
                .andExpect(jsonPath("$[1].tableNumber", is("Table 2")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testRemoveItemFromOrder_authenticatedAdminShouldSucceed() throws Exception {
        when(orderService.removeItemFromOrder(orderId, itemId)).thenReturn(testOrder);

        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testConfirmOrder_authenticatedAdminShouldSucceed() throws Exception {
        when(orderService.confirmOrder(orderId)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCompleteOrder_authenticatedAdminShouldSucceed() throws Exception {
        when(orderService.completeOrder(orderId)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/{orderId}/complete", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testCreateOrder_withValidJwtToken() throws Exception {
        String jwtToken = "valid.jwt.token";
        
        when(orderService.createOrder(anyString())).thenReturn(testOrder);
        
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(SecurityMockMvcRequestPostProcessors.user(adminUser)))
                .andExpect(status().isCreated());
    }

    // ERROR HANDLING TESTS

    @Test
    @WithAnonymousUser  
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.findOrderById(eq(orderId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testAddItemToOrder_OrderNotFound() throws Exception {
        when(orderService.addItemToOrder(
                eq(orderId),
                any(UUID.class), 
                anyString(), 
                any(Double.class), 
                anyInt())).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddItemRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testRemoveItemFromOrder_ItemNotFound() throws Exception {
        when(orderService.removeItemFromOrder(orderId, itemId))
                .thenThrow(new OrderItemNotFoundException(itemId, orderId));

        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isNotFound());
    }
} 