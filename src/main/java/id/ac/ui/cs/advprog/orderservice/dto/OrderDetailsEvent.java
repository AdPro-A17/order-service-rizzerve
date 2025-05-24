package id.ac.ui.cs.advprog.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsEvent {
    public enum EventType { CREATED, UPDATED, COMPLETED, CANCELLED }
    private EventType eventType;
    private UUID orderId;
    private String tableNumber;
    private String orderStatus;
    private double totalPrice;
    private List<OrderItemSummaryDto> items;
    private Instant occurredAt;
}