package id.ac.ui.cs.advprog.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    @NotNull(message = "Order items cannot be null")
    private List<OrderItemRequest> items;

    @NotEmpty(message = "Table number is required")
    private String tableNumber;

    private String couponCode;
    private String notes;
}