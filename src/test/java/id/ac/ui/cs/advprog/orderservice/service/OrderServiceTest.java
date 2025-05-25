package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.TableNotAvailableException;
import id.ac.ui.cs.advprog.orderservice.observer.OrderEventPublisher;
import id.ac.ui.cs.advprog.orderservice.client.TableServiceClient;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @Mock
    private TableServiceClient tableServiceClient;

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
    void testCreateOrder_WhenTableIsAvailable_ShouldCreateOrder() {
        // Arrange
        String tableNumber = "1";
        when(tableServiceClient.isTableAvailable(1)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(orderId);
            return orderToSave;
        });
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        // Act
        Order result = orderService.createOrder(tableNumber);

        // Assert
        assertNotNull(result);
        assertEquals(tableNumber, result.getTableNumber());
        assertEquals("NEW", result.getStatus());
        verify(tableServiceClient, times(1)).isTableAvailable(1);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testCreateOrder_WhenTableIsNotAvailable_ShouldThrowException() {
        // Arrange
        String tableNumber = "1";
        when(tableServiceClient.isTableAvailable(1)).thenReturn(false);

        // Act & Assert
        TableNotAvailableException exception = assertThrows(
                TableNotAvailableException.class,
                () -> orderService.createOrder(tableNumber)
        );

        assertEquals("Table " + tableNumber + " is not available or already occupied", exception.getMessage());
        verify(tableServiceClient, times(1)).isTableAvailable(1);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testCreateOrder_WhenInvalidTableNumber_ShouldThrowException() {
        // Arrange
        String invalidTableNumber = "invalid";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(invalidTableNumber)
        );

        assertEquals("Invalid table number format: " + invalidTableNumber, exception.getMessage());
        verify(tableServiceClient, never()).isTableAvailable(anyInt());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testFindOrderById_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.findOrderById(orderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindOrderById_WhenOrderDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.findOrderById(orderId);

        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindAllOrders_ShouldReturnAllOrders() {
        // Arrange
        Order order2 = new Order("T2");
        order2.setId(UUID.randomUUID());
        List<Order> orders = Arrays.asList(order, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.findAllOrders();

        // Assert
        assertEquals(2, result.size());
        assertEquals(orders, result);
        verify(orderRepository, times(1)).findAll();
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
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testAddItemToOrder_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.addItemToOrder(orderId, menuItemId, "Test Item", 10.0, 1)
        );

        assertEquals("Could not find order with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testUpdateItemQuantity_WhenItemExists_ShouldUpdateQuantity() {
        // Arrange
        order.addItem(item);
        int newQuantity = 3;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        // Act
        Order result = orderService.updateItemQuantity(orderId, orderItemId, newQuantity);

        // Assert
        assertNotNull(result);
        OrderItem updatedItem = result.getItems().stream()
                .filter(i -> i.getId().equals(orderItemId))
                .findFirst()
                .orElse(null);
        assertNotNull(updatedItem);
        assertEquals(newQuantity, updatedItem.getQuantity());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testUpdateItemQuantity_WhenItemNotFound_ShouldThrowException() {
        // Arrange
        UUID nonExistentItemId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        OrderItemNotFoundException exception = assertThrows(
                OrderItemNotFoundException.class,
                () -> orderService.updateItemQuantity(orderId, nonExistentItemId, 3)
        );

        assertEquals("Could not find order item with ID: " + nonExistentItemId + " in order with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testRemoveItemFromOrder_WhenItemExists_ShouldRemoveItem() {
        // Arrange
        order.addItem(item);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        // Act
        Order result = orderService.removeItemFromOrder(orderId, orderItemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().stream().noneMatch(i -> i.getId().equals(orderItemId)));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testRemoveItemFromOrder_WhenItemNotFound_ShouldThrowException() {
        // Arrange
        UUID nonExistentItemId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        OrderItemNotFoundException exception = assertThrows(
                OrderItemNotFoundException.class,
                () -> orderService.removeItemFromOrder(orderId, nonExistentItemId)
        );

        assertEquals("Could not find order item with ID: " + nonExistentItemId + " in order with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
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
    void testConfirmOrder_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.confirmOrder(orderId)
        );

        assertEquals("Could not find order with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
        }

    

    @Test
    void testCompleteOrder_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.completeOrder(orderId)
        );

        assertEquals("Could not find order with ID: " + orderId, exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(tableServiceClient, never()).releaseTable(anyInt(), any(UUID.class));
        verify(orderEventPublisher, never()).publishOrderEvent(any(OrderDetailsEvent.class));
    }

    @Test
    void testCompleteOrder_WhenInvalidTableNumber_ShouldNotFailOrderCompletion() {
        // Arrange - Set order to PROCESSING state first (required for completion)
        Order orderWithInvalidTable = new Order("invalid");
        orderWithInvalidTable.setId(orderId);
        orderWithInvalidTable.confirmOrder(); // Transition to PROCESSING state
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderWithInvalidTable));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        // Act
        Order result = orderService.completeOrder(orderId);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(orderWithInvalidTable);
        verify(tableServiceClient, never()).releaseTable(anyInt(), any(UUID.class));
        verify(orderEventPublisher, times(1)).publishOrderEvent(any(OrderDetailsEvent.class));
    }
}
