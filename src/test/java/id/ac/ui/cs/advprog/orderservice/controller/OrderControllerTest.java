package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.AddOrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.orderservice.dto.UpdateQuantityRequest;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.service.OrderService;
import id.ac.ui.cs.advprog.orderservice.client.MenuServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @Mock
    private MenuServiceClient menuServiceClient;

    @InjectMocks
    private OrderController orderController;

    private UUID orderId;
    private UUID itemId;
    private Order testOrder;
    private OrderItem testItem;
    private CreateOrderRequest validCreateRequest;
    private AddOrderItemRequest validAddItemRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        
        orderId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        testOrder = new Order("T1");
        testOrder.setId(orderId);
        
        testItem = new OrderItem(testOrder, UUID.randomUUID(), "Test Item", 1, 10.0);
        testItem.setId(itemId);
        testOrder.addItem(testItem);

        validCreateRequest = new CreateOrderRequest();
        validCreateRequest.setTableNumber("T1");

        validAddItemRequest = new AddOrderItemRequest();
        validAddItemRequest.setMenuItemId(UUID.randomUUID());
        validAddItemRequest.setMenuItemName("Test Item");
        validAddItemRequest.setPrice(10.0);
        validAddItemRequest.setQuantity(1);
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(anyString())).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.tableNumber", is("T1")));
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.findOrderById(orderId)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testGetAllOrders() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.findAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(orderId.toString())));
    }

    @Test
    void testAddItemToOrder() throws Exception {
        when(orderService.addItemToOrder(
                eq(orderId), 
                ArgumentMatchers.any(UUID.class), 
                anyString(), 
                anyDouble(), 
                anyInt())).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/{orderId}/items/manual", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testAddItemToOrder_OrderNotFound() throws Exception {
        when(orderService.addItemToOrder(
                eq(orderId), 
                ArgumentMatchers.any(UUID.class), 
                anyString(), 
                anyDouble(), 
                anyInt())).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(post("/api/orders/{orderId}/items/manual", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddItemRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateItemQuantity() throws Exception {
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setQuantity(3);
        
        when(orderService.updateItemQuantity(orderId, itemId, 3)).thenReturn(testOrder);

        mockMvc.perform(put("/api/orders/{orderId}/items/{itemId}", orderId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testUpdateItemQuantity_ItemNotFound() throws Exception {
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setQuantity(3);
        
        when(orderService.updateItemQuantity(orderId, itemId, 3))
                .thenThrow(new OrderItemNotFoundException(itemId, orderId));

        mockMvc.perform(put("/api/orders/{orderId}/items/{itemId}", orderId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveItemFromOrder() throws Exception {
        when(orderService.removeItemFromOrder(orderId, itemId)).thenReturn(testOrder);

        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testRemoveItemFromOrder_ItemNotFound() throws Exception {
        when(orderService.removeItemFromOrder(orderId, itemId))
                .thenThrow(new OrderItemNotFoundException(itemId, orderId));

        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testConfirmOrder() throws Exception {
        when(orderService.confirmOrder(orderId)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }
} 