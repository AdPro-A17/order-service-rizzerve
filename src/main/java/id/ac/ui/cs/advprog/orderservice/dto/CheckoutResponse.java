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
public class CheckoutResponse {
    private UUID checkoutId;
    private int tableNumber;
    private List<CheckoutItemResponse> items;
    private double totalPrice;
    private String couponCode;
    private double discountAmount;
    private double finalPrice;
    private String status;
    private String orderStatus;
}