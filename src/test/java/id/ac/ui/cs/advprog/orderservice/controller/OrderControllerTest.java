package id.ac.ui.cs.advprog.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.orderservice.dto.AddOrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.orderservice.dto.UpdateQuantityRequest;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.model.state.NewOrderState;
import id.ac.ui.cs.advprog.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    private UUID orderId;
    private UUID itemId;
    private Order testOrder;
    private OrderItem testItem;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        // Create test order
        testOrder = new Order("Table 1");
        testOrder.setId(orderId);
        testOrder.setState(new NewOrderState(testOrder));
        
        // Create test item
        testItem = new OrderItem();
        testItem.setId(itemId);
        testItem.setMenuItemId(UUID.randomUUID());
        testItem.setMenuItemName("Sushi Roll");
        testItem.setPrice(12.99);
        testItem.setQuantity(2);
        
        // Add item to order
        testOrder.addItem(testItem);
    }

    @Test
    void testCreateOrder() throws Exception {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTableNumber("Table 1");
        
        when(orderService.createOrder(anyString())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.tableNumber", is("Table 1")))
                .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    void testGetOrderById() throws Exception {
        // Arrange
        when(orderService.findOrderById(orderId)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.tableNumber", is("Table 1")))
                .andExpect(jsonPath("$.status", is("NEW")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].menuItemName", is("Sushi Roll")));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        // Arrange
        when(orderService.findOrderById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOrders() throws Exception {
        // Arrange
        Order order2 = new Order("Table 2");
        order2.setId(UUID.randomUUID());
        
        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderService.findAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(orderId.toString())))
                .andExpect(jsonPath("$[1].tableNumber", is("Table 2")));
    }

    @Test
    void testAddItemToOrder() throws Exception {
        // Arrange
        AddOrderItemRequest request = new AddOrderItemRequest();
        request.setMenuItemId(UUID.randomUUID());
        request.setMenuItemName("California Roll");
        request.setPrice(10.99);
        request.setQuantity(1);
        
        when(orderService.addItemToOrder(
                eq(orderId), 
                any(UUID.class), 
                anyString(), 
                any(Double.class), 
                anyInt())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testAddItemToOrder_OrderNotFound() throws Exception {
        // Arrange
        AddOrderItemRequest request = new AddOrderItemRequest();
        request.setMenuItemId(UUID.randomUUID());
        request.setMenuItemName("California Roll");
        request.setPrice(10.99);
        request.setQuantity(1);
        
        when(orderService.addItemToOrder(
                eq(orderId), 
                any(UUID.class), 
                anyString(), 
                any(Double.class), 
                anyInt())).thenThrow(new OrderNotFoundException(orderId));

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateItemQuantity() throws Exception {
        // Arrange
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setQuantity(3);
        
        when(orderService.updateItemQuantity(orderId, itemId, 3)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(put("/api/orders/{orderId}/items/{itemId}", orderId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testUpdateItemQuantity_ItemNotFound() throws Exception {
        // Arrange
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setQuantity(3);
        
        when(orderService.updateItemQuantity(orderId, itemId, 3))
                .thenThrow(new OrderItemNotFoundException(itemId, orderId));

        // Act & Assert
        mockMvc.perform(put("/api/orders/{orderId}/items/{itemId}", orderId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveItemFromOrder() throws Exception {
        // Arrange
        when(orderService.removeItemFromOrder(orderId, itemId)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testConfirmOrder() throws Exception {
        // Arrange
        when(orderService.confirmOrder(orderId)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }

    @Test
    void testCompleteOrder() throws Exception {
        // Arrange
        when(orderService.completeOrder(orderId)).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/complete", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.toString())));
    }
} 