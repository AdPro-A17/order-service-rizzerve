package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
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

    // Mock external dependencies if needed (e.g., MenuService, PaymentService)
    // @Mock
    // private MenuService menuService;

    @InjectMocks
    private OrderServiceImpl orderService; // Inject mocks into the implementation class

    private Order order;
    private OrderItem item;
    private UUID orderId;
    private UUID menuItemId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        menuItemId = UUID.randomUUID();

        order = new Order("T1");
        order.setId(orderId);
        // order.setState(new NewOrderState(order)); // Assume initial state is set

        item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setMenuItemId(menuItemId);
        item.setMenuItemName("Test Item");
        item.setQuantity(1);
        item.setPrice(10.0);
        item.setSubtotal(10.0);

        // Ensure OrderService interface and OrderServiceImpl exist
        fail("OrderService interface and OrderServiceImpl implementation needed.");
    }

    @Test
    void testCreateOrder() {
        // Mock repository save behavior
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(UUID.randomUUID()); // Simulate ID generation
            // Simulate setting initial state if done in service/constructor
            return o;
        });

        Order createdOrder = orderService.createOrder("T1");

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals("T1", createdOrder.getTableNumber());
        // assertEquals("NEW", createdOrder.getStatus()); // Check initial status
        assertTrue(createdOrder.getItems().isEmpty());
        assertEquals(0.0, createdOrder.getTotalPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
        fail("Implementation needed. Ensure initial state is set correctly.");
    }

    @Test
    void testFindOrderById_Found() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.findOrderById(orderId);

        assertTrue(foundOrder.isPresent());
        assertEquals(order, foundOrder.get());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindOrderById_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderService.findOrderById(orderId);

        assertFalse(foundOrder.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindAllOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        orders.add(new Order("T2"));

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> foundOrders = orderService.findAllOrders();

        assertEquals(2, foundOrders.size());
        assertEquals(orders, foundOrders);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testAddItemToOrder() {
        // Assume OrderItem details (name, price) might be fetched from another service
        // or passed directly.
        // Let's assume they are passed in AddItemRequestDTO or similar
        String itemName = "Nasi Goreng";
        double itemPrice = 15000.0;
        int quantity = 1;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        // Mock saving the updated order
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Need a method like: Order addItemToOrder(UUID orderId, UUID menuItemId, String name, double price, int quantity)
        // Order updatedOrder = orderService.addItemToOrder(orderId, menuItemId, itemName, itemPrice, quantity);

        // assertNotNull(updatedOrder);
        // assertEquals(1, updatedOrder.getItems().size());
        // OrderItem addedItem = updatedOrder.getItems().get(0);
        // assertEquals(menuItemId, addedItem.getMenuItemId());
        // assertEquals(itemName, addedItem.getMenuItemName());
        // assertEquals(itemPrice, addedItem.getPrice());
        // assertEquals(quantity, addedItem.getQuantity());
        // assertEquals(itemPrice * quantity, addedItem.getSubtotal());
        // assertEquals(itemPrice * quantity, updatedOrder.getTotalPrice()); // Check total price update
        // verify(orderRepository, times(1)).findById(orderId);
        // verify(orderRepository, times(1)).save(order);
        fail("addItemToOrder method implementation needed in OrderService.");
    }

    @Test
    void testAddItemToOrder_OrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // assertThrows(OrderNotFoundException.class, () -> {
        //     orderService.addItemToOrder(orderId, menuItemId, "Test", 10.0, 1);
        // });

        // verify(orderRepository, times(1)).findById(orderId);
        // verify(orderRepository, never()).save(any(Order.class));
        fail("addItemToOrder should handle OrderNotFoundException (or similar).");
    }

    // Add tests for updateItemQuantity, removeItemFromOrder

    @Test
    void testUpdateItemQuantity() {
        // Setup: Add an item first
        order.getItems().add(item);
        order.calculateTotalPrice();
        UUID orderItemId = item.getId();
        int newQuantity = 3;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Order updatedOrder = orderService.updateItemQuantity(orderId, orderItemId, newQuantity);

        // assertNotNull(updatedOrder);
        // assertEquals(1, updatedOrder.getItems().size());
        // OrderItem updatedItem = updatedOrder.getItems().get(0);
        // assertEquals(newQuantity, updatedItem.getQuantity());
        // assertEquals(item.getPrice() * newQuantity, updatedItem.getSubtotal());
        // assertEquals(item.getPrice() * newQuantity, updatedOrder.getTotalPrice()); // Check total price update
        // verify(orderRepository, times(1)).findById(orderId);
        // verify(orderRepository, times(1)).save(order);
        fail("updateItemQuantity method implementation needed.");
    }

     @Test
    void testRemoveItemFromOrder() {
        // Setup: Add an item first
        order.getItems().add(item);
        order.calculateTotalPrice();
        UUID orderItemId = item.getId();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Order updatedOrder = orderService.removeItemFromOrder(orderId, orderItemId);

        // assertNotNull(updatedOrder);
        // assertTrue(updatedOrder.getItems().isEmpty());
        // assertEquals(0.0, updatedOrder.getTotalPrice()); // Check total price update
        // verify(orderRepository, times(1)).findById(orderId);
        // verify(orderRepository, times(1)).save(order);
        fail("removeItemFromOrder method implementation needed.");
    }


    // Add tests for state transitions (confirm, cancel, complete)
    // These tests should verify that the correct state methods are called
    // And that the repository saves the updated state

    @Test
    void testConfirmOrder() {
        // Mock the initial state and its behavior
        OrderState initialState = mock(OrderState.class);
        order.setState(initialState); // Manually set mock state

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Assume confirmOrder delegates to the state object
        // Order confirmedOrder = orderService.confirmOrder(orderId);

        // verify(orderRepository, times(1)).findById(orderId);
        // verify(initialState, times(1)).confirmOrder(); // Verify state method called
        // verify(orderRepository, times(1)).save(order); // Verify save is called after state change
        // assertNotNull(confirmedOrder);
        fail("confirmOrder method implementation needed, including state delegation and saving.");
    }

    @Test
    void testCancelOrder() {
        // Mock the current state
        OrderState currentState = mock(OrderState.class);
        order.setState(currentState);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Order cancelledOrder = orderService.cancelOrder(orderId);

        // verify(orderRepository, times(1)).findById(orderId);
        // verify(currentState, times(1)).cancelOrder(); // Verify state method called
        // verify(orderRepository, times(1)).save(order);
        // assertNotNull(cancelledOrder);
        fail("cancelOrder method implementation needed.");
    }

    @Test
    void testCompleteOrder() {
         // Mock the current state
        OrderState currentState = mock(OrderState.class);
        order.setState(currentState);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Order completedOrder = orderService.completeOrder(orderId);

        // verify(orderRepository, times(1)).findById(orderId);
        // verify(currentState, times(1)).completeOrder(); // Verify state method called
        // verify(orderRepository, times(1)).save(order);
        // assertNotNull(completedOrder);
        fail("completeOrder method implementation needed.");
    }

     @Test
    void testStateTransitionFailure() {
        // Example: Trying to cancel an already completed order
        OrderState completedState = mock(OrderState.class);
        order.setState(completedState);

        // Simulate the state throwing an exception for an invalid transition
        doThrow(new IllegalStateException("Cannot cancel a completed order")).when(completedState).cancelOrder();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // assertThrows(IllegalStateException.class, () -> {
        //     orderService.cancelOrder(orderId);
        // });

        // verify(orderRepository, times(1)).findById(orderId);
        // verify(completedState, times(1)).cancelOrder();
        // verify(orderRepository, never()).save(any(Order.class)); // Should not save if transition failed
        fail("Need to test handling of invalid state transitions (e.g., exceptions).");
    }
} 