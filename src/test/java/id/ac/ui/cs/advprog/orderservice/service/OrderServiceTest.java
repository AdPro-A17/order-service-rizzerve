package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.model.state.*;
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

    // No need to mock states if we test interactions via the Order object

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

        // Use a real Order object to test interactions
        order = new Order("T1");
        order.setId(orderId); // Manually set ID for testing findById
        // Initial state is NEW

        item = new OrderItem(order, menuItemId, "Test Item", 1, 10.0);
        item.setId(orderItemId); // Manually set ID

        // Don't add item here, add it in specific tests
    }

    @Test
    void testCreateOrder() {
        // Arrange: Mock repository save to return the saved order with potential ID update
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            // Simulate ID generation if not done in constructor
            if (savedOrder.getId() == null) {
                 savedOrder.setId(UUID.randomUUID());
            }
            assertTrue(savedOrder.getState() instanceof NewOrderState, "Order should be in NEW state on creation");
            assertEquals("T1", savedOrder.getTableNumber());
            return savedOrder;
        });

        // Act
        Order createdOrder = orderService.createOrder("T1");

        // Assert
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals("T1", createdOrder.getTableNumber());
        assertTrue(createdOrder.getState() instanceof NewOrderState);
        assertEquals("NEW", createdOrder.getStatus());
        assertTrue(createdOrder.getItems().isEmpty());
        assertEquals(0.0, createdOrder.getTotalPrice());
        verify(orderRepository, times(1)).save(any(Order.class)); // Verify save was called
    }

    @Test
    void testFindOrderById_Found() {
        // Arrange: Mock repository returning the order
        // Ensure state is hydrated when findById is called in service
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> foundOrderOpt = orderService.findOrderById(orderId);

        // Assert
        assertTrue(foundOrderOpt.isPresent());
        Order foundOrder = foundOrderOpt.get();
        assertEquals(order, foundOrder);
        assertNotNull(foundOrder.getState()); // Check state was hydrated
        assertTrue(foundOrder.getState() instanceof NewOrderState);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindOrderById_NotFound() {
        // Arrange: Mock repository returning empty
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> foundOrderOpt = orderService.findOrderById(orderId);

        // Assert
        assertFalse(foundOrderOpt.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindAllOrders() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Order order2 = new Order("T2");
        order2.setState(new ProcessingOrderState(order2)); // Simulate different state
        orders.add(order);
        orders.add(order2);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> foundOrders = orderService.findAllOrders();

        // Assert
        assertEquals(2, foundOrders.size());
        assertEquals(orders, foundOrders);
        // Check states were hydrated
        assertNotNull(foundOrders.get(0).getState());
        assertNotNull(foundOrders.get(1).getState());
        assertTrue(foundOrders.get(0).getState() instanceof NewOrderState);
        assertTrue(foundOrders.get(1).getState() instanceof ProcessingOrderState);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testAddItemToOrder() {
        // Arrange
        String itemName = "Nasi Goreng";
        double itemPrice = 15000.0;
        int quantity = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        // Mock save to return the modified order AND simulate OrderItem ID generation
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            // Simulate item ID generation if the item doesn't have one yet
            orderToSave.getItems().stream()
                .filter(item -> item.getId() == null)
                .forEach(item -> item.setId(UUID.randomUUID())); // Assign a dummy ID
            return orderToSave;
        });

        // Act
        Order updatedOrder = orderService.addItemToOrder(orderId, menuItemId, itemName, itemPrice, quantity);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        OrderItem addedItem = updatedOrder.getItems().get(0);
        assertNotNull(addedItem.getId()); // Should have an ID after being added/saved
        assertEquals(menuItemId, addedItem.getMenuItemId());
        assertEquals(itemName, addedItem.getMenuItemName());
        assertEquals(itemPrice, addedItem.getPrice());
        assertEquals(quantity, addedItem.getQuantity());
        assertEquals(itemPrice * quantity, addedItem.getSubtotal());
        assertEquals(itemPrice * quantity, updatedOrder.getTotalPrice()); // Check total price update
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order); // Verify save with the updated order
    }

    @Test
    void testAddItemToOrder_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.addItemToOrder(orderId, menuItemId, "Test", 10.0, 1);
        });
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class)); // Save should not be called
    }


    @Test
    void testUpdateItemQuantity() {
        // Arrange: Add an item first to the order object
        order.addItem(item); // Add the item with ID orderItemId
        assertEquals(1, order.getItems().size());
        assertEquals(10.0, order.getTotalPrice());

        int newQuantity = 3;
        double expectedSubtotal = item.getPrice() * newQuantity; // 10.0 * 3 = 30.0
        double expectedTotal = expectedSubtotal;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order updatedOrder = orderService.updateItemQuantity(orderId, orderItemId, newQuantity);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        OrderItem updatedItem = updatedOrder.getItems().get(0);
        assertEquals(orderItemId, updatedItem.getId());
        assertEquals(newQuantity, updatedItem.getQuantity());
        assertEquals(expectedSubtotal, updatedItem.getSubtotal());
        assertEquals(expectedTotal, updatedOrder.getTotalPrice());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

     @Test
    void testUpdateItemQuantity_ItemNotFound() {
        // Arrange: Order exists but does not contain the item
        UUID nonExistentItemId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order)); // Order is found
        assertTrue(order.getItems().isEmpty()); // Ensure item is not in order

        // Act & Assert
        assertThrows(OrderItemNotFoundException.class, () -> {
            orderService.updateItemQuantity(orderId, nonExistentItemId, 5);
        });
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

     @Test
    void testRemoveItemFromOrder() {
        // Arrange: Add item first
        order.addItem(item);
        assertEquals(1, order.getItems().size());
        assertEquals(10.0, order.getTotalPrice());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order updatedOrder = orderService.removeItemFromOrder(orderId, orderItemId);

        // Assert
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.getItems().isEmpty());
        assertEquals(0.0, updatedOrder.getTotalPrice());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

     @Test
    void testRemoveItemFromOrder_ItemNotFound() {
        // Arrange: Order exists but item doesn't
        UUID nonExistentItemId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertTrue(order.getItems().isEmpty());

        // Act & Assert
        assertThrows(OrderItemNotFoundException.class, () -> {
            orderService.removeItemFromOrder(orderId, nonExistentItemId);
        });
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }


    // --- State Transition Tests --- //

    @Test
    void testConfirmOrder() {
        // Arrange: Order is in NEW state
        assertTrue(order.getState() instanceof NewOrderState);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order confirmedOrder = orderService.confirmOrder(orderId);

        // Assert
        assertNotNull(confirmedOrder);
        assertTrue(confirmedOrder.getState() instanceof ProcessingOrderState, "State should transition to Processing");
        assertEquals("PROCESSING", confirmedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order); // Verify save is called after state change
    }

    @Test
    void testCompleteOrder_FromProcessing() {
        // Arrange: Manually set state to Processing
         order.setState(new ProcessingOrderState(order));
         assertTrue(order.getState() instanceof ProcessingOrderState);
         when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
         when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order completedOrder = orderService.completeOrder(orderId);

        // Assert
        assertNotNull(completedOrder);
        assertTrue(completedOrder.getState() instanceof CompletedOrderState);
        assertEquals("COMPLETED", completedOrder.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

     @Test
    void testStateTransitionFailure_ConfirmProcessingOrder() {
        // Arrange: Set state to Processing
        order.setState(new ProcessingOrderState(order));
        assertTrue(order.getState() instanceof ProcessingOrderState);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            orderService.confirmOrder(orderId);
        });
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        assertTrue(order.getState() instanceof ProcessingOrderState); // State remains Processing
    }
} 