package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.model.state.NewOrderState;
import id.ac.ui.cs.advprog.orderservice.model.state.OrderState;
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

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        order = new Order("T1");

        item1 = mock(OrderItem.class);
        when(item1.getSubtotal()).thenReturn(10.0);
        when(item1.getId()).thenReturn(UUID.randomUUID());

        item2 = mock(OrderItem.class);
        when(item2.getSubtotal()).thenReturn(20.0);
        when(item2.getId()).thenReturn(UUID.randomUUID());

        fail("Order model and OrderState interface/implementations needed.");
    }

    @Test
    void testOrderConstructorSetsTableNumberAndInitialState() {
        Order newOrder = new Order("T5");
        assertNotNull(newOrder.getId());
        assertEquals("T5", newOrder.getTableNumber());
        assertTrue(newOrder.getItems().isEmpty());
        assertEquals(0.0, newOrder.getTotalPrice());
        assertNotNull(newOrder.getState());
        // assertTrue(newOrder.getState() instanceof NewOrderState);
        // assertEquals("NEW", newOrder.getStatus()); // Assuming getStatus delegates to state
        fail("Constructor should set initial state (e.g., NewOrderState).");
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
    }

    @Test
    void testGetSetTotalPrice() {
        // Total price is normally calculated, but test setter if exists
        order.setTotalPrice(50.0);
        assertEquals(50.0, order.getTotalPrice());
    }

    @Test
    void testCalculateTotalPrice() {
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        order.setItems(items);

        order.calculateTotalPrice(); // Assuming this method exists

        assertEquals(30.0, order.getTotalPrice());
        verify(item1, times(1)).getSubtotal();
        verify(item2, times(1)).getSubtotal();
        fail("calculateTotalPrice method implementation needed.");
    }

     @Test
    void testCalculateTotalPrice_EmptyList() {
        order.setItems(new ArrayList<>());
        order.calculateTotalPrice();
        assertEquals(0.0, order.getTotalPrice());
    }

    @Test
    void testSetState() {
        OrderState mockState = mock(OrderState.class);
        order.setState(mockState);
        assertEquals(mockState, order.getState());
    }

    // Tests related to adding/removing items and recalculating total
    @Test
    void testAddItemRecalculatesTotal() {
        order.addItem(item1); // Assuming addItem method exists and calls calculateTotalPrice
        assertEquals(10.0, order.getTotalPrice());
        assertEquals(1, order.getItems().size());

        order.addItem(item2);
        assertEquals(30.0, order.getTotalPrice());
        assertEquals(2, order.getItems().size());
        fail("addItem method implementation needed, should recalculate total.");
    }

    @Test
    void testRemoveItemRecalculatesTotal() {
        order.addItem(item1);
        order.addItem(item2); // Total is 30.0

        order.removeItem(item1.getId()); // Assuming removeItem(UUID) exists and calls calculateTotalPrice
        assertEquals(20.0, order.getTotalPrice());
        assertEquals(1, order.getItems().size());
        assertFalse(order.getItems().contains(item1));
        assertTrue(order.getItems().contains(item2));
        fail("removeItem method implementation needed, should recalculate total.");
    }

    @Test
    void testUpdateItemRecalculatesTotal() {
        OrderItem realItem = new OrderItem();
        realItem.setId(UUID.randomUUID());
        realItem.setPrice(5.0);
        realItem.setQuantity(2); // Subtotal 10.0
        realItem.setSubtotal(10.0); // Explicitly set for test

        order.addItem(realItem);
        assertEquals(10.0, order.getTotalPrice());

        // Simulate updating the quantity and subtotal of the item within the order
        // This might happen via OrderService -> Order -> OrderItem
        // For this test, let's assume Order has an updateItem method
        OrderItem updatedItemData = new OrderItem();
        updatedItemData.setQuantity(4);
        updatedItemData.setSubtotal(20.0); // New calculated subtotal

        order.updateItem(realItem.getId(), updatedItemData); // Assuming updateItem(UUID, OrderItem) exists

        assertEquals(20.0, order.getTotalPrice()); // Total should be updated
        assertEquals(1, order.getItems().size());
        assertEquals(4, order.getItems().get(0).getQuantity()); // Check if quantity updated
        fail("updateItem method implementation needed, should recalculate total.");
    }

    // Test State Pattern Delegation
    @Test
    void testConfirmOrderDelegatesToState() {
        OrderState mockState = mock(OrderState.class);
        order.setState(mockState);
        order.confirmOrder(); // Assuming Order has confirmOrder()
        verify(mockState, times(1)).confirmOrder();
        fail("Order methods (confirm, cancel, etc.) should delegate to the current state object.");
    }

    @Test
    void testCancelOrderDelegatesToState() {
        OrderState mockState = mock(OrderState.class);
        order.setState(mockState);
        order.cancelOrder(); // Assuming Order has cancelOrder()
        verify(mockState, times(1)).cancelOrder();
        fail("Order methods (confirm, cancel, etc.) should delegate to the current state object.");
    }

     @Test
    void testCompleteOrderDelegatesToState() {
        OrderState mockState = mock(OrderState.class);
        order.setState(mockState);
        order.completeOrder(); // Assuming Order has completeOrder()
        verify(mockState, times(1)).completeOrder();
        fail("Order methods (confirm, cancel, etc.) should delegate to the current state object.");
    }

    @Test
    void testGetStatusDelegatesToState() {
         OrderState mockState = mock(OrderState.class);
         when(mockState.getStatus()).thenReturn("MOCKED_STATUS");
         order.setState(mockState);

        // String status = order.getStatus(); // Assuming Order has getStatus()
        // assertEquals("MOCKED_STATUS", status);
         verify(mockState, times(1)).getStatus();
         fail("Order getStatus() should delegate to the current state object.");
    }
} 