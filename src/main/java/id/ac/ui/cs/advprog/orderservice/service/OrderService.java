package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    Order createOrder(String tableNumber);
    Optional<Order> findOrderById(UUID orderId);
    List<Order> findAllOrders();
    Order addItemToOrder(UUID orderId, UUID menuItemId, String menuItemName, double price, int quantity);
    Order updateItemQuantity(UUID orderId, UUID orderItemId, int newQuantity);
    Order removeItemFromOrder(UUID orderId, UUID orderItemId);
    Order confirmOrder(UUID orderId);
    Order completeOrder(UUID orderId);
} 