package id.ac.ui.cs.advprog.orderservice.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CheckoutRequest {
    private UUID orderId;
    private String couponCode;
}