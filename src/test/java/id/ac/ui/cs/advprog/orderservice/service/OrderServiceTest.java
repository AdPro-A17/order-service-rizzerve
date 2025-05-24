package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.observer.OrderEventPublisher;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderItem item;
    private UUID orderId;
    private UUID menuItemId;
    private UUID orderItemId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        menuItemId = UUID.randomUUID();
        orderItemId = UUID.randomUUID();

        order = new Order("T1");
        order.setId(orderId);

        item = new OrderItem(order, menuItemId, "Test Item", 1, 10.0);
        item.setId(orderItemId);
    }

    @Test
    void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            if (savedOrder.getId() == null) {
                savedOrder.setId(UUID.randomUUID());
            }
            assertEquals("T1", savedOrder.getTableNumber());
            return savedOrder;
        });
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order createdOrder = orderService.createOrder("T1");

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals("T1", createdOrder.getTableNumber());
        assertEquals("NEW", createdOrder.getStatus());
        assertTrue(createdOrder.getItems().isEmpty());
        assertEquals(0.0, createdOrder.getTotalPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testAddItemToOrder() {
        String itemName = "Nasi Goreng";
        double itemPrice = 15000.0;
        int quantity = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.getItems().stream()
                    .filter(item -> item.getId() == null)
                    .forEach(item -> item.setId(UUID.randomUUID()));
            return orderToSave;
        });
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order updatedOrder = orderService.addItemToOrder(orderId, menuItemId, itemName, itemPrice, quantity);

        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        OrderItem addedItem = updatedOrder.getItems().get(0);
        assertNotNull(addedItem.getId());
        assertEquals(menuItemId, addedItem.getMenuItemId());
        assertEquals(itemName, addedItem.getMenuItemName());
        assertEquals(itemPrice, addedItem.getPrice());
        assertEquals(quantity, addedItem.getQuantity());
        assertEquals(itemPrice * quantity, addedItem.getSubtotal());
        assertEquals(itemPrice * quantity, updatedOrder.getTotalPrice());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testConfirmOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order confirmedOrder = orderService.confirmOrder(orderId);

        assertNotNull(confirmedOrder);
        assertEquals("PROCESSING", confirmedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testCompleteOrder() {
        order.confirmOrder(); // Move to PROCESSING first
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order completedOrder = orderService.completeOrder(orderId);

        assertNotNull(completedOrder);
        assertEquals("COMPLETED", completedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }
}
