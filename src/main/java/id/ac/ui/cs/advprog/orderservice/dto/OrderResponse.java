package id.ac.ui.cs.advprog.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String tableNumber;
    private String status;
    private double totalPrice;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private UUID menuItemId;
        private String menuItemName;
        private double price;
        private int quantity;
        private double subtotal;
    }
} 