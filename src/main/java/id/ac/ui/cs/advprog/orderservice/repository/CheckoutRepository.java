package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {
}