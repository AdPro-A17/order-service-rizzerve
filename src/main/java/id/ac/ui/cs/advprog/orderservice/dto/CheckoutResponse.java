package id.ac.ui.cs.advprog.orderservice.dto;

import id.ac.ui.cs.advprog.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {
    private UUID orderId;
    private UUID checkoutId;
    private String tableNumber;
    private LocalDateTime orderTime;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String appliedCouponCode;
    private List<OrderItemResponse> items;
    private LocalDateTime checkoutTime;
    private boolean successful;
    private String notes;
}