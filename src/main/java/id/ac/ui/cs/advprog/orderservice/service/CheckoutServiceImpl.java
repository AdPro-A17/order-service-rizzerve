package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.orderservice.dto.OrderItemRequest;
import id.ac.ui.cs.advprog.orderservice.dto.OrderItemResponse;
import id.ac.ui.cs.advprog.orderservice.enums.OrderStatus;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.orderservice.repository.MenuItemRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CheckoutRepository checkoutRepository;
    private final CouponService couponService;
    private final UserService userService;

    @Override
    @Transactional
    public CheckoutResponse processCheckout(CheckoutRequest request) {
        UUID userId = getCurrentUserId();

        Order order = createOrder(request, userId);

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            applyCoupon(order, request.getCouponCode());
        }

        orderRepository.save(order);

        Checkout checkout = createCheckoutRecord(order, request.getNotes());
        checkoutRepository.save(checkout);

        return mapToCheckoutResponse(order, checkout);
    }

    private Order createOrder(CheckoutRequest request, UUID userId) {
        List<OrderItem> orderItems = createOrderItems(request.getItems());

        BigDecimal subtotal = calculateSubtotal(orderItems);

        Order order = Order.builder()
                .userId(userId)
                .tableNumber(request.getTableNumber())
                .orderTime(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .discount(BigDecimal.ZERO)
                .total(subtotal)
                .items(orderItems)
                .build();

        orderItems.forEach(item -> item.setOrder(order));

        return order;
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + request.getMenuItemId()));

        if (!menuItem.isAvailable()) {
            throw new RuntimeException("Menu item is not available: " + menuItem.getName());
        }

        BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        return OrderItem.builder()
                .menuItemId(menuItem.getId())
                .menuItemName(menuItem.getName())
                .pricePerItem(menuItem.getPrice())
                .quantity(request.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    private BigDecimal calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void applyCoupon(Order order, String couponCode) {
        var couponValidation = couponService.validateCoupon(couponCode, order.getSubtotal());

        if (!couponValidation.isValid()) {
            throw new RuntimeException("Invalid coupon: " + couponValidation.getMessage());
        }

        order.setAppliedCouponCode(couponCode);
        order.setDiscount(couponValidation.getDiscountAmount());
        order.setTotal(order.getSubtotal().subtract(couponValidation.getDiscountAmount()));
    }

    private Checkout createCheckoutRecord(Order order, String notes) {
        return Checkout.builder()
                .order(order)
                .userId(order.getUserId())
                .checkoutTime(LocalDateTime.now())
                .successful(true)
                .notes(notes)
                .build();
    }

    private CheckoutResponse mapToCheckoutResponse(Order order, Checkout checkout) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .checkoutId(checkout.getId())
                .tableNumber(order.getTableNumber())
                .orderTime(order.getOrderTime())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .appliedCouponCode(order.getAppliedCouponCode())
                .items(itemResponses)
                .checkoutTime(checkout.getCheckoutTime())
                .successful(checkout.isSuccessful())
                .notes(checkout.getNotes())
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItemId())
                .menuItemName(item.getMenuItemName())
                .pricePerItem(item.getPricePerItem())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserIdFromAuthentication(authentication);
    }
}