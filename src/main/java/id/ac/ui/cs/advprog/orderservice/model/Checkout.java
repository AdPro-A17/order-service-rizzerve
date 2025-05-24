package id.ac.ui.cs.advprog.orderservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "checkouts")
@Getter
@Setter
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private int tableNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "checkout_id")
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double totalPrice;

    private String couponCode;
    private double discountAmount;
    private double finalPrice;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Checkout() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "SUBMITTED";
    }

    public void setItems(List<OrderItem> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
    }
}