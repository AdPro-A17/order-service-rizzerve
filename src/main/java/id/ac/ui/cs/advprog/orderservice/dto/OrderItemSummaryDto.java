package id.ac.ui.cs.advprog.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemSummaryDto {
    private UUID menuItemId;
    private String menuItemName;
    private int quantity;
    private double price;
    private double subtotal;
}