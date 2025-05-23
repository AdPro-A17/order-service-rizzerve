package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.model.state.*;
import id.ac.ui.cs.advprog.orderservice.observer.OrderEventPublisher;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private UUID orderId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        order = new Order("T1");
        order.setId(orderId);
    }

    @Test
    void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order createdOrder = orderService.createOrder("T1");

        assertNotNull(createdOrder);
        assertEquals("T1", createdOrder.getTableNumber());
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testFindOrderById_Found() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.findOrderById(orderId);

        assertTrue(foundOrder.isPresent());
        assertEquals(order, foundOrder.get());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void testFindOrderById_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderService.findOrderById(orderId);

        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void testAddItemToOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order updatedOrder = orderService.addItemToOrder(orderId, UUID.randomUUID(), "Item", 10.0, 2);

        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testAddItemToOrder_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.addItemToOrder(orderId, UUID.randomUUID(), "Item", 10.0, 2));
    }

    @Test
    void testUpdateItemQuantity() {
        OrderItem item = new OrderItem();
        item.setId(itemId);
        order.addItem(item);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order updatedOrder = orderService.updateItemQuantity(orderId, itemId, 3);

        assertNotNull(updatedOrder);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testUpdateItemQuantity_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.updateItemQuantity(orderId, itemId, 3));
    }

    @Test
    void testUpdateItemQuantity_ItemNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(OrderItemNotFoundException.class, () ->
                orderService.updateItemQuantity(orderId, itemId, 3));
    }

    @Test
    void testRemoveItemFromOrder() {
        OrderItem item = new OrderItem();
        item.setId(itemId);
        order.addItem(item);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order updatedOrder = orderService.removeItemFromOrder(orderId, itemId);

        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.getItems().isEmpty());
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testRemoveItemFromOrder_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.removeItemFromOrder(orderId, itemId));
    }

    @Test
    void testRemoveItemFromOrder_ItemNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(OrderItemNotFoundException.class, () ->
                orderService.removeItemFromOrder(orderId, itemId));
    }

    @Test
    void testConfirmOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order confirmedOrder = orderService.confirmOrder(orderId);

        assertNotNull(confirmedOrder);
        assertTrue(confirmedOrder.getState() instanceof ProcessingOrderState);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testConfirmOrder_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.confirmOrder(orderId));
    }

    @Test
    void testCompleteOrder() {
        order.setState(new ProcessingOrderState(order));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order completedOrder = orderService.completeOrder(orderId);

        assertNotNull(completedOrder);
        assertTrue(completedOrder.getState() instanceof CompletedOrderState);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testCompleteOrder_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderService.completeOrder(orderId));
    }
}