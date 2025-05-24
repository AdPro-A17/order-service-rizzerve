package id.ac.ui.cs.advprog.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @Entity  // Temporarily commented out
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String code;

    private BigDecimal discountAmount;
    private Integer discountPercentage;
    private BigDecimal minimumPurchase;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean active;
    private UUID menuItemId;

    @Column(name = "created_by")
    private UUID createdBy;
    public static Coupon create(String code, BigDecimal discountAmount, Integer discountPercentage,
                                BigDecimal minimumPurchase, LocalDateTime validFrom,
                                LocalDateTime validUntil, boolean active, UUID menuItemId,
                                UUID createdBy) {
        return Coupon.builder()
                .code(code)
                .discountAmount(discountAmount)
                .discountPercentage(discountPercentage)
                .minimumPurchase(minimumPurchase)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .active(active)
                .menuItemId(menuItemId)
                .createdBy(createdBy)
                .build();
    }
}