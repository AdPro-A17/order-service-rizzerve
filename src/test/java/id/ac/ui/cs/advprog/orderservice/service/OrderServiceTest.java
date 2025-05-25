package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testCreateOrder() {
        // Mock table service calls
        when(tableServiceClient.isTableAvailable(1)).thenReturn(true);
        doNothing().when(tableServiceClient).reserveTable(eq(1), any(UUID.class));
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            if (savedOrder.getId() == null) {
                savedOrder.setId(UUID.randomUUID());
            }
            assertEquals("1", savedOrder.getTableNumber());
            return savedOrder;
        });
        doNothing().when(orderEventPublisher).publishOrderEvent(any(OrderDetailsEvent.class));

        Order createdOrder = orderService.createOrder("1");

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());
        assertEquals("1", createdOrder.getTableNumber());
        assertEquals("NEW", createdOrder.getStatus());
        assertTrue(createdOrder.getItems().isEmpty());
        assertEquals(0.0, createdOrder.getTotalPrice());
        
        verify(tableServiceClient, times(1)).isTableAvailable(1);
        verify(tableServiceClient, times(1)).reserveTable(eq(1), any(UUID.class));
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
        OrderItem addedItem = updatedOrder.getItems().getFirst();
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
}
