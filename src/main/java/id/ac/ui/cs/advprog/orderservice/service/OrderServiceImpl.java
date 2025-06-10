package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.orderservice.dto.OrderItemSummaryDto;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.TableNotAvailableException;
import id.ac.ui.cs.advprog.orderservice.observer.OrderEventPublisher;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import id.ac.ui.cs.advprog.orderservice.client.TableServiceClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final TableServiceClient tableServiceClient;
    private final MeterRegistry meterRegistry;

    private final Counter orderCreatedCounter;
    private final Counter orderUpdatedCounter;
    private final Counter orderCompletedCounter;
    private final Counter orderRetrievedCounter;
    

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher, TableServiceClient tableServiceClient, MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.tableServiceClient = tableServiceClient;
        this.meterRegistry = meterRegistry;
        this.orderCreatedCounter = Counter.builder("order.created")
                .description("Number of orders created")
                .register(meterRegistry);
        this.orderUpdatedCounter = Counter.builder("order.updated")
                .description("Number of orders updated")
                .register(meterRegistry);
        this.orderCompletedCounter = Counter.builder("order.completed")
                .description("Number of orders completed")
                .register(meterRegistry);
        this.orderRetrievedCounter = Counter.builder("order.retrieved")
                .description("Number of orders retrieved")
                .register(meterRegistry);
    }

    private OrderDetailsEvent mapToOrderDetailsEvent(Order order, OrderDetailsEvent.EventType eventType) {
        List<OrderItemSummaryDto> itemSummaries = order.getItems().stream()
                .map(item -> OrderItemSummaryDto.builder()
                        .menuItemId(item.getMenuItemId())
                        .menuItemName(item.getMenuItemName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderDetailsEvent.builder()
                .eventType(eventType)
                .orderId(order.getId())
                .tableNumber(order.getTableNumber())
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .items(itemSummaries)
                .occurredAt(Instant.now())
                .build();
    }

    @Override
    @Transactional
    public Order createOrder(String tableNumber) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            log.debug("Creating order for table: {}", tableNumber);
            
            // Validate table number format
            int tableNum;
            try {
                tableNum = Integer.parseInt(tableNumber);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid table number format: " + tableNumber);
            }

            // Check availability of table via TableServiceClient
            log.debug("Checking if table {} is available", tableNum);
            if (!tableServiceClient.isTableAvailable(tableNum)) {
                throw new TableNotAvailableException("Table " + tableNumber + " is not available or already occupied");
            }

             // Create order & set initial state
            Order order = new Order(tableNumber); // State pattern starts here!
            Order savedOrder = orderRepository.save(order);
            log.debug("Order saved with ID: {}, Status: {}", savedOrder.getId(), savedOrder.getStatus());
            
            // Publish order created event - table service will listen and update table status automatically
            OrderDetailsEvent event = mapToOrderDetailsEvent(savedOrder, OrderDetailsEvent.EventType.CREATED);
            log.debug("Publishing event - Type: {}, OrderID: {}, Table: {}", event.getEventType(), event.getOrderId(), event.getTableNumber());
            orderEventPublisher.publishOrderEvent(event);
            log.debug("Event published successfully");
            
            orderCreatedCounter.increment();
            return savedOrder;
        } finally {
            sample.stop(Timer.builder("order.operation.duration")
                    .tag("operation", "create")
                    .description("Time taken for order create operations")
                    .register(meterRegistry));
        }
    }

    @Override
    public Optional<Order> findOrderById(UUID orderId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Optional<Order> order = orderRepository.findById(orderId);
            order.ifPresent(Order::getState);
            if (order.isPresent()) {
                orderRetrievedCounter.increment();
            }
            return order;
        } finally {
            sample.stop(Timer.builder("order.operation.duration")
                    .tag("operation", "getById")
                    .description("Time taken for order getById operations")
                    .register(meterRegistry));
        }
    }

    @Override
    public List<Order> findAllOrders() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            List<Order> orders = orderRepository.findAll();
            orders.forEach(Order::getState);
            orderRetrievedCounter.increment(orders.size());
            return orders;
        } finally {
            sample.stop(Timer.builder("order.operation.duration")
                    .tag("operation", "getAll")
                    .description("Time taken for order getAll operations")
                    .register(meterRegistry));
        }
    }

    @Override
    @Transactional
    public Order addItemToOrder(UUID orderId, UUID menuItemId, String menuItemName, double price, int quantity) {
        long startTime = System.nanoTime();
        try {
            Order order = findOrderByIdOrThrow(orderId);
            OrderItem newItem = new OrderItem(order, menuItemId, menuItemName, quantity, price);
            order.addItem(newItem);
            Order updatedOrder = orderRepository.save(order);
            if (!STATUS_COMPLETED.equals(updatedOrder.getStatus()) && !STATUS_CANCELLED.equals(updatedOrder.getStatus())) {
                orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
            }
            orderUpdatedCounter.increment();
            return updatedOrder;
        } finally {
            Timer.builder("order.operation.duration")
                    .tag("operation", "addItem")
                    .register(meterRegistry)
                    .record(System.nanoTime() - startTime, java.util.concurrent.TimeUnit.NANOSECONDS);
        }
    }

    @Override
    @Transactional
    public Order updateItemQuantity(UUID orderId, UUID orderItemId, int newQuantity) {
        long startTime = System.nanoTime();
        try {
            Order order = findOrderByIdOrThrow(orderId);
            OrderItem itemToUpdate = order.getItems().stream()
                    .filter(item -> item.getId().equals(orderItemId))
                    .findFirst()
                    .orElseThrow(() -> new OrderItemNotFoundException(orderItemId, orderId));
            itemToUpdate.setQuantity(newQuantity);
            order.calculateTotalPrice();
            Order updatedOrder = orderRepository.save(order);
            if (!STATUS_COMPLETED.equals(updatedOrder.getStatus()) && !STATUS_CANCELLED.equals(updatedOrder.getStatus())) {
                orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
            }
            orderUpdatedCounter.increment();
            return updatedOrder;
        } finally {
            Timer.builder("order.operation.duration")
                    .tag("operation", "updateItem")
                    .register(meterRegistry)
                    .record(System.nanoTime() - startTime, java.util.concurrent.TimeUnit.NANOSECONDS);
        }
    }

    @Override
    @Transactional
    public Order removeItemFromOrder(UUID orderId, UUID orderItemId) {
        long startTime = System.nanoTime();
        try {
            Order order = findOrderByIdOrThrow(orderId);
            boolean itemExists = order.getItems().stream().anyMatch(item -> item.getId().equals(orderItemId));
            if (!itemExists) {
                throw new OrderItemNotFoundException(orderItemId, orderId);
            }
            order.removeItem(orderItemId);
            Order updatedOrder = orderRepository.save(order);
            if (!STATUS_COMPLETED.equals(updatedOrder.getStatus()) && !STATUS_CANCELLED.equals(updatedOrder.getStatus())) {
                orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
            }
            orderUpdatedCounter.increment();
            return updatedOrder;
        } finally {
            Timer.builder("order.operation.duration")
                    .tag("operation", "removeItem")
                    .register(meterRegistry)
                    .record(System.nanoTime() - startTime, java.util.concurrent.TimeUnit.NANOSECONDS);
        }
    }

    @Override
    @Transactional
    public Order confirmOrder(UUID orderId) {
        long startTime = System.nanoTime();
        try {
            Order order = findOrderByIdOrThrow(orderId);
            order.confirmOrder();
            Order updatedOrder = orderRepository.save(order);

            try {
                int tableNum = Integer.parseInt(updatedOrder.getTableNumber());
                tableServiceClient.updateTableStatus(tableNum, updatedOrder.getId(), updatedOrder.getStatus());

            } catch (NumberFormatException e) {
                log.error("Invalid table number format: {}", updatedOrder.getTableNumber());
            }

            orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
            orderUpdatedCounter.increment();
            return updatedOrder;
        } finally {
            Timer.builder("order.operation.duration")
                    .tag("operation", "confirm")
                    .register(meterRegistry)
                    .record(System.nanoTime() - startTime, java.util.concurrent.TimeUnit.NANOSECONDS);
        }
    }

    @Override
    @Transactional
    public Order completeOrder(UUID orderId) {
        long startTime = System.nanoTime();
        try {
            Order order = findOrderByIdOrThrow(orderId);
            order.completeOrder();
            Order updatedOrder = orderRepository.save(order);
            
            // Release the table when order is completed
            try {
                int tableNum = Integer.parseInt(updatedOrder.getTableNumber());
                tableServiceClient.releaseTable(tableNum, updatedOrder.getId());
            } catch (NumberFormatException e) {
                // Log error but don't fail the order completion
                log.error("Invalid table number format when releasing table: {}", updatedOrder.getTableNumber());
            }
            
            orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.COMPLETED));
            orderCompletedCounter.increment();
            return updatedOrder;
        } finally {
            Timer.builder("order.operation.duration")
                    .tag("operation", "complete")
                    .register(meterRegistry)
                    .record(System.nanoTime() - startTime, java.util.concurrent.TimeUnit.NANOSECONDS);
        }
    }

    private Order findOrderByIdOrThrow(UUID orderId) {
        return findOrderById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

}
