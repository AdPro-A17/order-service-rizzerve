package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByAvailableTrue();
}
