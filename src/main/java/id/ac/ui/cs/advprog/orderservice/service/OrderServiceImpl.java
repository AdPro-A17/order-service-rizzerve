package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import id.ac.ui.cs.advprog.orderservice.dto.OrderItemSummaryDto;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.observer.OrderEventPublisher;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
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
                .collect(Collectors.toList());

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
        Order order = new Order(tableNumber);
        Order savedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(savedOrder, OrderDetailsEvent.EventType.CREATED));
        return savedOrder;
    }

    @Override
    public Optional<Order> findOrderById(UUID orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(Order::getState);
        return order;
    }

    @Override
    public List<Order> findAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(Order::getState);
        return orders;
    }

    @Override
    @Transactional
    public Order addItemToOrder(UUID orderId, UUID menuItemId, String menuItemName, double price, int quantity) {
        Order order = findOrderByIdOrThrow(orderId);
        OrderItem newItem = new OrderItem(order, menuItemId, menuItemName, quantity, price);
        order.addItem(newItem);
        Order updatedOrder = orderRepository.save(order);
        if (!"COMPLETED".equals(updatedOrder.getStatus()) && !"CANCELLED".equals(updatedOrder.getStatus())) {
            orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
        }
        return updatedOrder;
    }

    @Override
    @Transactional
    public Order updateItemQuantity(UUID orderId, UUID orderItemId, int newQuantity) {
        Order order = findOrderByIdOrThrow(orderId);
        OrderItem itemToUpdate = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(orderItemId, orderId));
        itemToUpdate.setQuantity(newQuantity);
        order.calculateTotalPrice();
        Order updatedOrder = orderRepository.save(order);
        if (!"COMPLETED".equals(updatedOrder.getStatus()) && !"CANCELLED".equals(updatedOrder.getStatus())) {
            orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
        }
        return updatedOrder;
    }

    @Override
    @Transactional
    public Order removeItemFromOrder(UUID orderId, UUID orderItemId) {
        Order order = findOrderByIdOrThrow(orderId);
        boolean itemExists = order.getItems().stream().anyMatch(item -> item.getId().equals(orderItemId));
        if (!itemExists) {
            throw new OrderItemNotFoundException(orderItemId, orderId);
        }
        order.removeItem(orderItemId);
        Order updatedOrder = orderRepository.save(order);
        if (!"COMPLETED".equals(updatedOrder.getStatus()) && !"CANCELLED".equals(updatedOrder.getStatus())) {
            orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
        }
        return updatedOrder;
    }

    @Override
    @Transactional
    public Order confirmOrder(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.confirmOrder();
        Order updatedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.UPDATED));
        return updatedOrder;
    }

    @Override
    @Transactional
    public Order completeOrder(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        order.completeOrder();
        Order updatedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderEvent(mapToOrderDetailsEvent(updatedOrder, OrderDetailsEvent.EventType.COMPLETED));
        return updatedOrder;
    }

    private Order findOrderByIdOrThrow(UUID orderId) {
        return findOrderById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}