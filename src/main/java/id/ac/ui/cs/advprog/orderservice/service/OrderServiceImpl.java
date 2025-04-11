package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order createOrder(String tableNumber) {
        Order order = new Order(tableNumber);
        // Initial state (NEW) is set in Order constructor
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findOrderById(UUID orderId) {
        // Note: .getState() call ensures state is hydrated after loading
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(Order::getState); // Initialize transient state
        return order;
    }

    @Override
    public List<Order> findAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(Order::getState); // Initialize transient state for all
        return orders;
    }

    @Override
    @Transactional
    public Order addItemToOrder(UUID orderId, UUID menuItemId, String menuItemName, double price, int quantity) {
        Order order = findOrderByIdOrThrow(orderId);
        // Add validation: Cannot add items if order is not in NEW state?
        // if (!"NEW".equals(order.getStatus())) {
        //     throw new IllegalStateException("Cannot add items to an order that is not NEW.");
        // }
        OrderItem newItem = new OrderItem(order, menuItemId, menuItemName, quantity, price);
        order.addItem(newItem); // This also recalculates total
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateItemQuantity(UUID orderId, UUID orderItemId, int newQuantity) {
        Order order = findOrderByIdOrThrow(orderId);
        // Add validation: Cannot update items if order is not in NEW state?

        // Find the specific item within the order's item list
        OrderItem itemToUpdate = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(orderItemId, orderId));

        itemToUpdate.setQuantity(newQuantity); // This recalculates item subtotal
        order.calculateTotalPrice(); // Recalculate order total
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order removeItemFromOrder(UUID orderId, UUID orderItemId) {
        Order order = findOrderByIdOrThrow(orderId);
        // Add validation: Cannot remove items if order is not in NEW state?

        // Check if item exists before removal to throw specific exception
        boolean itemExists = order.getItems().stream().anyMatch(item -> item.getId().equals(orderItemId));
        if (!itemExists) {
            throw new OrderItemNotFoundException(orderItemId, orderId);
        }

        order.removeItem(orderItemId); // This also recalculates total
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order confirmOrder(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.confirmOrder(); // Delegate to state object
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order completeOrder(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.completeOrder(); // Delegate to state object
        return orderRepository.save(order);
    }

    // Helper method to find order or throw exception
    private Order findOrderByIdOrThrow(UUID orderId) {
        return findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
} 