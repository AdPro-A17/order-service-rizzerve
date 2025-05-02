package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
