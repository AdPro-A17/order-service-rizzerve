package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface OrderService {

    /**
     * Creates a new order for a given table number.
     * @param tableNumber The table number for the order.
     * @return The newly created Order object.
     */
    Order createOrder(String tableNumber);

    /**
     * Finds an order by its unique ID.
     * @param orderId The UUID of the order.
     * @return An Optional containing the Order if found, otherwise empty.
     */
    Optional<Order> findOrderById(UUID orderId);

    /**
     * Retrieves all existing orders.
     * @return A List of all Order objects.
     */
    List<Order> findAllOrders();

    /**
     * Adds an item to an existing order.
     * Fetches item details (name, price) from the menu service.
     * @param orderId The ID of the order to add the item to.
     * @param menuItemId The ID of the menu item.
     * @param quantity The quantity of the item to add.
     * @return The updated Order object.
     * @throws OrderNotFoundException if the orderId does not exist.
     * @throws MenuItemNotFoundException if the menuItemId does not exist or is unavailable.
     * @throws IllegalArgumentException if quantity is invalid.
     */
    Order addItemToOrder(UUID orderId, UUID menuItemId, int quantity);

    /**
     * Updates the quantity of an existing item within an order.
     * @param orderId The ID of the order.
     * @param orderItemId The ID of the order item to update.
     * @param newQuantity The new quantity for the item.
     * @return The updated Order object.
     * @throws OrderNotFoundException if the orderId does not exist.
     * @throws OrderItemNotFoundException if the orderItemId does not exist within the order.
     * @throws IllegalArgumentException if newQuantity is invalid.
     */
    Order updateItemQuantity(UUID orderId, UUID orderItemId, int newQuantity);

    /**
     * Removes an item from an order.
     * @param orderId The ID of the order.
     * @param orderItemId The ID of the order item to remove.
     * @return The updated Order object.
     * @throws OrderNotFoundException if the orderId does not exist.
     * @throws OrderItemNotFoundException if the orderItemId does not exist within the order.
     */
    Order removeItemFromOrder(UUID orderId, UUID orderItemId);

    /**
     * Confirms an order, typically moving it to a 'PROCESSING' state.
     * Delegates the action to the Order's current state.
     * @param orderId The ID of the order to confirm.
     * @return The updated Order object.
     * @throws OrderNotFoundException if the orderId does not exist.
     * @throws IllegalStateException if the order cannot be confirmed in its current state.
     */
    Order confirmOrder(UUID orderId);

    /**
     * Marks an order as completed.
     * Delegates the action to the Order's current state.
     * @param orderId The ID of the order to complete.
     * @return The updated Order object.
     * @throws OrderNotFoundException if the orderId does not exist.
     * @throws IllegalStateException if the order cannot be completed in its current state.
     */
    Order completeOrder(UUID orderId);

    // ASYNC METHODS

    /**
     * Asynchronously get all orders
     */
    CompletableFuture<List<Order>> getAllOrdersAsync();

    /**
     * Asynchronously get an order by ID
     */
    CompletableFuture<Order> getOrderByIdAsync(UUID orderId);

    /**
     * Asynchronously create a new order
     */
    CompletableFuture<Order> createOrderAsync(String tableNumber);

    /**
     * Asynchronously add item to order
     */
    CompletableFuture<Order> addItemToOrderAsync(UUID orderId, UUID menuItemId, int quantity);

    /**
     * Asynchronously complete an order
     */
    CompletableFuture<Order> completeOrderAsync(UUID orderId);

    // Define custom exception classes if needed (e.g., OrderNotFoundException, OrderItemNotFoundException)
} 