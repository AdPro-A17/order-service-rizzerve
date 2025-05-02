package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import id.ac.ui.cs.advprog.orderservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemRepository menuItemRepository;

    @GetMapping("/public/menu-items")
    public ResponseEntity<List<MenuItem>> getAllPublicMenuItems() {
        return ResponseEntity.ok(menuItemRepository.findByAvailableTrue());
    }

    @GetMapping("/customer/menu-items")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<MenuItem>> getAllCustomerMenuItems() {
        return ResponseEntity.ok(menuItemRepository.findByAvailableTrue());
    }
}