package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List; // Add if using findByTableNumber or similar list-returning methods

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByTableNumber(String tableNumber);
    List<Order> findByStatusString(String statusString);
    List<Order> findByTableNumberAndStatusStringNotIn(String tableNumber, List<String> statuses);
} 