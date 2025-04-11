package id.ac.ui.cs.advprog.orderservice.model;

import id.ac.ui.cs.advprog.orderservice.model.state.NewOrderState;
import id.ac.ui.cs.advprog.orderservice.model.state.OrderState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String tableNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double totalPrice;

    // Using a String to store the state class name for persistence
    @Column(name = "status", nullable = false)
    private String statusString;

    // Transient state object - managed by the application logic
    @Transient
    private OrderState currentState;

    // Default constructor for JPA
    public Order() {
        this.items = new ArrayList<>(); // Ensure list is initialized
        // Initial state might be set later or upon creation via service
    }

    // Constructor for creating a new order
    public Order(String tableNumber) {
        this(); // Call default constructor
        this.tableNumber = tableNumber;
        this.totalPrice = 0.0;
        // Set initial state
        setState(new NewOrderState(this));
    }

    // Custom setter for state to update both the object and the persistent string
    public void setState(OrderState newState) {
        this.currentState = newState;
        this.statusString = newState.getStatus();
    }

    // Custom getter for state - reconstructs state object after loading from DB
    public OrderState getState() {
        if (currentState == null && statusString != null) {
            // Re-hydrate the state object based on the stored status string
            this.currentState = StateFactory.createState(this.statusString, this);
        }
        return currentState;
    }

    // Delegate actions to the current state object
    public void confirmOrder() {
        getState().confirmOrder();
    }

    public void completeOrder() {
        getState().completeOrder();
    }

    // Method to get the status string (delegates to state)
    public String getStatus() {
        return getState().getStatus();
    }

    // Method to calculate the total price based on items
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    // Method to add an item
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this); // Set the back-reference
        calculateTotalPrice();
    }

    // Method to remove an item by its ID
    public void removeItem(UUID orderItemId) {
        items.removeIf(item -> item.getId().equals(orderItemId));
        calculateTotalPrice();
    }

    // Method to update an existing item (find by ID, update fields, recalculate)
    public void updateItem(UUID orderItemId, OrderItem updatedItemData) {
        OrderItem itemToUpdate = items.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        // Update relevant fields (e.g., quantity) - price might be fixed
        itemToUpdate.setQuantity(updatedItemData.getQuantity());
        // Note: Subtotal is recalculated automatically in OrderItem's setQuantity

        calculateTotalPrice(); // Recalculate order total
    }


    // Lombok will generate standard getters/setters for id, tableNumber, items, totalPrice, statusString
    // We have custom ones for 'state' management.

    // You might need @PostLoad annotated method if state hydration needs to happen right after entity load
    // @PostLoad
    // void initializeState() {
    //     getState(); // Ensure currentState is initialized
    // }

    // Equals and HashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}

// Simple factory to re-create state objects from stored string status
// This could be its own class or a static inner class
class StateFactory {
    public static OrderState createState(String status, Order order) {
        return switch (status) {
            case "NEW" -> new NewOrderState(order);
            case "PROCESSING" -> new id.ac.ui.cs.advprog.orderservice.model.state.ProcessingOrderState(order);
            case "COMPLETED" -> new id.ac.ui.cs.advprog.orderservice.model.state.CompletedOrderState(order);
            default -> throw new IllegalArgumentException("Unknown order status: " + status);
        };
    }
} 