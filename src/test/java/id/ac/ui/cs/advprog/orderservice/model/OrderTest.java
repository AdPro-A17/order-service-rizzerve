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
        order = new Order("T1");

        item1 = mock(OrderItem.class);
        when(item1.getSubtotal()).thenReturn(10.0);
        when(item1.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(item1).setOrder(any(Order.class));

        item2 = mock(OrderItem.class);
        when(item2.getSubtotal()).thenReturn(20.0);
        when(item2.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(item2).setOrder(any(Order.class));

        realItem = new OrderItem();
        realItem.setId(UUID.randomUUID());
        realItem.setMenuItemId(UUID.randomUUID());
        realItem.setMenuItemName("Real Item");
        realItem.setPrice(5.0);
        realItem.setQuantity(2);
    }

    @Test
    void testOrderConstructorSetsTableNumberAndInitialState() {
        Order newOrder = new Order("T5");
        assertNull(newOrder.getId());
        assertEquals("T5", newOrder.getTableNumber());
        assertTrue(newOrder.getItems().isEmpty());
        assertEquals(0.0, newOrder.getTotalPrice());
        assertNotNull(newOrder.getState());
        assertTrue(newOrder.getState() instanceof NewOrderState);
        assertEquals("NEW", newOrder.getStatus());
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
        order.setTotalPrice(50.0);
        assertEquals(50.0, order.getTotalPrice());
    }

    @Test
    void testAddItem() {
        order.addItem(realItem);
        assertEquals(1, order.getItems().size());
        assertEquals(realItem, order.getItems().get(0));
        assertEquals(10.0, order.getTotalPrice());
    }

    @Test
    void testRemoveItem() {
        order.addItem(realItem);
        order.removeItem(realItem.getId());
        assertTrue(order.getItems().isEmpty());
        assertEquals(0.0, order.getTotalPrice());
    }

    @Test
    void testConfirmOrder() {
        order.confirmOrder();
        assertTrue(order.getState() instanceof ProcessingOrderState);
        assertEquals("PROCESSING", order.getStatus());
    }

    @Test
    void testCompleteOrder() {
        order.confirmOrder();
        order.completeOrder();
        assertTrue(order.getState() instanceof CompletedOrderState);
        assertEquals("COMPLETED", order.getStatus());
    }

    @Test
    void testGetStatus() {
        assertEquals("NEW", order.getStatus());
        order.confirmOrder();
        assertEquals("PROCESSING", order.getStatus());
        order.completeOrder();
        assertEquals("COMPLETED", order.getStatus());
    }

    @Test
    void testSetState() {
        OrderState newState = new ProcessingOrderState(order);
        order.setState(newState);
        assertEquals(newState, order.getState());
        assertEquals("PROCESSING", order.getStatus());
    }
}