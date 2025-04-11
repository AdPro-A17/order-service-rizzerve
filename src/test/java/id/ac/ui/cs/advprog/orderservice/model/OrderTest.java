package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.model.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderTest {

    private Order order;
    private UUID id;
    private OrderItem item1;
    private OrderItem item2;
    private OrderItem realItem;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        order = new Order("T1"); // Creates order with NewOrderState

        // Mock items for total price calculation tests
        item1 = mock(OrderItem.class);
        when(item1.getSubtotal()).thenReturn(10.0);
        when(item1.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(item1).setOrder(any(Order.class)); // Stub setOrder if needed

        item2 = mock(OrderItem.class);
        when(item2.getSubtotal()).thenReturn(20.0);
        when(item2.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(item2).setOrder(any(Order.class)); // Stub setOrder

        // Real item for update tests
        realItem = new OrderItem();
        realItem.setId(UUID.randomUUID());
        realItem.setMenuItemId(UUID.randomUUID());
        realItem.setMenuItemName("Real Item");
        realItem.setPrice(5.0);
        realItem.setQuantity(2); // Initial subtotal should be 10.0

    }

    @Test
    void testOrderConstructorSetsTableNumberAndInitialState() {
        Order newOrder = new Order("T5");
        assertNull(newOrder.getId()); // ID should be null before persistence
        assertEquals("T5", newOrder.getTableNumber());
        assertTrue(newOrder.getItems().isEmpty());
        assertEquals(0.0, newOrder.getTotalPrice());
        assertNotNull(newOrder.getState());
        assertTrue(newOrder.getState() instanceof NewOrderState);
        assertEquals("NEW", newOrder.getStatus()); // Check initial status string
    }

    @Test
    void testGetSetId() {
        order.setId(id);
        assertEquals(id, order.getId());
    }

    @Test
    void testGetSetTableNumber() {
        order.setTableNumber("T2");
        assertEquals("T2", order.getTableNumber());
    }

    @Test
    void testGetSetItems() {
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        order.setItems(items);
        assertEquals(items, order.getItems());
        assertEquals(1, order.getItems().size());
        // Note: Manually setting items list bypasses automatic total calculation
    }

    @Test
    void testGetSetTotalPrice() {
        // Total price is normally calculated, but test setter if exists
        order.setTotalPrice(50.0);
        assertEquals(50.0, order.getTotalPrice());
    }

    @Test
    void testCalculateTotalPrice() {
        // Use addItem to ensure relationships and calculations are triggered
        order.addItem(item1);
        order.addItem(item2);

        // Calculation is done within addItem, just check the result
        assertEquals(30.0, order.getTotalPrice());

        // Explicitly call calculateTotalPrice for good measure
        order.calculateTotalPrice();
        assertEquals(30.0, order.getTotalPrice());

        // Verify mocks (subtotal was called implicitly via addItem -> calculateTotalPrice)
        verify(item1, atLeastOnce()).getSubtotal();
        verify(item2, atLeastOnce()).getSubtotal();
    }

     @Test
    void testCalculateTotalPrice_EmptyList() {
        order.setItems(new ArrayList<>()); // Set directly for empty test
        order.calculateTotalPrice();
        assertEquals(0.0, order.getTotalPrice());
    }

    @Test
    void testSetStateUpdatesStateAndStatusString() {
        OrderState mockState = mock(ProcessingOrderState.class);
        when(mockState.getStatus()).thenReturn("PROCESSING");

        order.setState(mockState);

        assertEquals(mockState, order.getState());
        assertEquals("PROCESSING", order.getStatusString()); // Check persisted string
        assertEquals("PROCESSING", order.getStatus()); // Check status via getter (delegates to state)
    }

    @Test
    void testGetStateHydration() {
        // Simulate loading from DB where only statusString is set
        Order loadedOrder = new Order();
        loadedOrder.setId(UUID.randomUUID());
        loadedOrder.setStatusString("PROCESSING");
        // currentState should be null initially

        // Accessing getState should trigger hydration via StateFactory
        OrderState state = loadedOrder.getState();
        assertNotNull(state);
        assertTrue(state instanceof ProcessingOrderState);
        assertEquals("PROCESSING", state.getStatus());

        // Accessing again should return the same hydrated instance
        assertSame(state, loadedOrder.getState());
    }

    // Tests related to adding/removing items and recalculating total
    @Test
    void testAddItemRecalculatesTotalAndSetsBackReference() {
        order.addItem(item1);
        assertEquals(10.0, order.getTotalPrice());
        assertEquals(1, order.getItems().size());
        verify(item1, times(1)).setOrder(order); // Verify back-reference set

        order.addItem(item2);
        assertEquals(30.0, order.getTotalPrice());
        assertEquals(2, order.getItems().size());
        verify(item2, times(1)).setOrder(order);
    }

    @Test
    void testRemoveItemRecalculatesTotal() {
        order.addItem(item1);
        order.addItem(item2); // Total is 30.0
        UUID item1Id = item1.getId();

        order.removeItem(item1Id);
        assertEquals(20.0, order.getTotalPrice());
        assertEquals(1, order.getItems().size());
        assertFalse(order.getItems().stream().anyMatch(i -> i.getId().equals(item1Id)));
        assertTrue(order.getItems().contains(item2));
    }

    @Test
    void testUpdateItemRecalculatesTotal() {
        order.addItem(realItem); // Initial total 10.0
        assertEquals(10.0, order.getTotalPrice());
        assertEquals(5.0, realItem.getPrice());
        assertEquals(2, realItem.getQuantity());
        assertEquals(10.0, realItem.getSubtotal());

        // Create dummy object with updated data (only quantity matters here for update logic)
        OrderItem updatedItemData = new OrderItem();
        updatedItemData.setQuantity(4);

        // Call updateItem on the order
        order.updateItem(realItem.getId(), updatedItemData);

        // Verify Order's total price
        assertEquals(20.0, order.getTotalPrice()); // 5.0 * 4
        assertEquals(1, order.getItems().size());

        // Verify the actual item within the order was updated
        OrderItem itemInOrder = order.getItems().get(0);
        assertEquals(4, itemInOrder.getQuantity());
        assertEquals(20.0, itemInOrder.getSubtotal()); // Check item's subtotal also updated
    }

     @Test
    void testUpdateItem_ItemNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            order.updateItem(UUID.randomUUID(), new OrderItem());
        });
    }

    // Test State Pattern Delegation
    @Test
    void testConfirmOrderDelegatesToState() {
        OrderState mockState = mock(OrderState.class);
        order.setState(mockState);
        order.confirmOrder();
        verify(mockState, times(1)).confirmOrder();
    }

    @Test
    void testGetStatusDelegatesToState() {
         // Arrange: Set a specific state (e.g., Processing) using setState
         ProcessingOrderState processingState = new ProcessingOrderState(order);
         order.setState(processingState);

         // Act: Call getStatus(), which internally calls getState() -> state.getStatus()
         String status = order.getStatus();

         // Assert
         assertEquals("PROCESSING", status);
         // Verify that the getStatus() method was called on the *actual* state object held by the order
         // We can't directly verify the mock we created if getState() hydrates a new one,
         // so we check the outcome (correct status string).
         // We can also assert the type of the current state:
         assertTrue(order.getState() instanceof ProcessingOrderState);
         // If we want to ensure delegation happened, we'd need to spy on the state object
         // or trust that if the status is correct, the delegation worked.
    }

     @Test
    void testEqualsAndHashCode() {
        Order order1 = new Order("T1");
        UUID generatedId = UUID.randomUUID();
        order1.setId(generatedId);

        Order order2 = new Order("T2");
        order2.setId(generatedId);

        Order order3 = new Order("T1");
        order3.setId(UUID.randomUUID());

        Order order4 = new Order("T1"); // ID is different (or null if not set)

        assertEquals(order1, order2); // Equal based on ID
        assertNotEquals(order1, order3);
        assertNotEquals(order1, null);
        assertNotEquals(order1, new Object());
        assertEquals(order1.hashCode(), order2.hashCode()); // Hash based on ID
    }

} 