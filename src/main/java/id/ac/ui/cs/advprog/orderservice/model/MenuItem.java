package id.ac.ui.cs.advprog.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
    private String category;
    private double averageRating;

    @Column(name = "created_by")
    private UUID createdBy;
    public static MenuItem create(String name, String description, BigDecimal price,
                                  boolean available, String category, double averageRating,
                                  UUID createdBy) {
        return MenuItem.builder()
                .name(name)
                .description(description)
                .price(price)
                .available(available)
                .category(category)
                .averageRating(averageRating)
                .createdBy(createdBy)
                .build();
    }
}