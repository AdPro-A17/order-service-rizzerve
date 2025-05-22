package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.AddOrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.orderservice.dto.OrderResponse;
import id.ac.ui.cs.advprog.orderservice.dto.UpdateQuantityRequest;
import id.ac.ui.cs.advprog.orderservice.exception.OrderItemNotFoundException;
import id.ac.ui.cs.advprog.orderservice.exception.OrderNotFoundException;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Customer creates order by selecting table
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getTableNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToOrderResponse(order));
    }

    // Customer can view their order
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        return orderService.findOrderById(orderId)
                .map(order -> ResponseEntity.ok(mapToOrderResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin can view all orders (restaurant dashboard)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.findAllOrders().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // Customer adds items to order (menu selection)
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItemToOrder(
            @PathVariable UUID orderId,
            @RequestBody AddOrderItemRequest request) {
        try {
            Order updatedOrder = orderService.addItemToOrder(
                    orderId,
                    request.getMenuItemId(),
                    request.getMenuItemName(),
                    request.getPrice(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(mapToOrderResponse(updatedOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Customer can update item quantities
    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderResponse> updateItemQuantity(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @RequestBody UpdateQuantityRequest request) {
        try {
            Order updatedOrder = orderService.updateItemQuantity(orderId, itemId, request.getQuantity());
            return ResponseEntity.ok(mapToOrderResponse(updatedOrder));
        } catch (OrderNotFoundException | OrderItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Only admin can remove items from orders
    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> removeItemFromOrder(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId) {
        try {
            Order updatedOrder = orderService.removeItemFromOrder(orderId, itemId);
            return ResponseEntity.ok(mapToOrderResponse(updatedOrder));
        } catch (OrderNotFoundException | OrderItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Customer can confirm order (checkout)
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID orderId) {
        try {
            Order updatedOrder = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(mapToOrderResponse(updatedOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Only admin can mark orders as complete
    @PostMapping("/{orderId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable UUID orderId) {
        try {
            Order updatedOrder = orderService.completeOrder(orderId);
            return ResponseEntity.ok(mapToOrderResponse(updatedOrder));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .tableNumber(order.getTableNumber())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .items(order.getItems().stream()
                        .map(item -> OrderResponse.OrderItemResponse.builder()
                                .id(item.getId())
                                .menuItemId(item.getMenuItemId())
                                .menuItemName(item.getMenuItemName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
} 