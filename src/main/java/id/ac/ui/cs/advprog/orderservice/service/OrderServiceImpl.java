package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.client.MenuServiceClient;
import id.ac.ui.cs.advprog.orderservice.exception.MenuItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, MenuServiceClient menuServiceClient) {
        this.orderRepository = orderRepository;
        this.menuServiceClient = menuServiceClient;
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
    public Order addItemToOrder(UUID orderId, UUID menuItemId, int quantity) {
        Order order = findOrderByIdOrThrow(orderId);
        
        // Fetch menu item details from menu service
        MenuServiceClient.MenuItemResponse menuItem = menuServiceClient.getMenuItemById(menuItemId);
        if (menuItem == null) {
            throw new MenuItemNotFoundException(menuItemId);
        }
        
        // Check if menu item is available
        if (!menuItem.getAvailable()) {
            throw new MenuItemNotFoundException(menuItemId, "Item is currently unavailable");
        }
        
        log.info("Adding menu item {} to order {}: {} x {} at ${}", 
                menuItemId, orderId, menuItem.getName(), quantity, menuItem.getPrice());
        
        OrderItem newItem = new OrderItem(order, menuItemId, menuItem.getName(), quantity, menuItem.getPrice());
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

    // ASYNC METHODS

    /**
     * Asynchronously get all orders
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<Order>> getAllOrdersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<Order> orders = orderRepository.findAll();
            orders.forEach(Order::getState); // Initialize transient state for all
            return orders;
        });
    }

    /**
     * Asynchronously get an order by ID
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<Order> getOrderByIdAsync(UUID orderId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Order> order = orderRepository.findById(orderId);
            order.ifPresent(Order::getState); // Initialize transient state
            return order.orElse(null);
        });
    }

    /**
     * Asynchronously create a new order
     */
    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Order> createOrderAsync(String tableNumber) {
        return CompletableFuture.supplyAsync(() -> {
            if (tableNumber == null || tableNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Table number cannot be empty");
            }
            Order order = new Order(tableNumber);
            return orderRepository.save(order);
        });
    }

    /**
     * Asynchronously add item to order
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<Order> addItemToOrderAsync(UUID orderId, UUID menuItemId, int quantity) {
        Order result = addItemToOrder(orderId, menuItemId, quantity);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Asynchronously complete an order
     */
    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Order> completeOrderAsync(UUID orderId) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = findOrderByIdOrThrow(orderId);
            order.completeOrder();
            return orderRepository.save(order);
        });
    }

    // Helper method to find order or throw exception
    private Order findOrderByIdOrThrow(UUID orderId) {
        return findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
} 