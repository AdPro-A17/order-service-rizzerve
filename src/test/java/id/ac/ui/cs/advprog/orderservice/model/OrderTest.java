package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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