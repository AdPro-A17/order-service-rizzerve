package id.ac.ui.cs.advprog.orderservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void addItem_ShouldAddItemToOrderAndSetOrderInItem() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        order.addItem(orderItem);

        assertTrue(order.getItems().contains(orderItem));
        assertEquals(order, orderItem.getOrder());
    }

    @Test
    void removeItem_ShouldRemoveItemFromOrderAndNullifyOrderInItem() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        order.addItem(orderItem);

        order.removeItem(orderItem);

        assertFalse(order.getItems().contains(orderItem));
        assertNull(orderItem.getOrder());
    }
}