package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List; // Add if using findByTableNumber or similar list-returning methods

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // Spring Data JPA will automatically provide implementations for common methods
    // like save(), findById(), findAll(), deleteById()

    // Define custom query methods if needed
    // Example: Find all orders for a specific table number
    // List<Order> findByTableNumber(String tableNumber);

    // Example: Find all orders with a specific status string
    // List<Order> findByStatusString(String statusString);
} 