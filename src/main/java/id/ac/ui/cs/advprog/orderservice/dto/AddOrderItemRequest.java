package id.ac.ui.cs.advprog.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderItemRequest {
    @NotNull(message = "Menu item ID is required")
    private UUID menuItemId;
    
    @NotBlank(message = "Menu item name is required")
    private String menuItemName;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
} 