package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkouts")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public ResponseEntity<Checkout> createCheckout(@RequestBody CheckoutRequest request) {
        Checkout checkout = checkoutService.createCheckout(request);
        return ResponseEntity.ok(checkout);
    }

    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<Checkout>> getCheckoutsByTable(@PathVariable String tableNumber) {
        List<Checkout> checkouts = checkoutService.getCheckoutsByTable(tableNumber);
        return ResponseEntity.ok(checkouts);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Checkout>> getAllCheckouts() {
        List<Checkout> checkouts = checkoutService.getCheckoutsByStatus("SUBMITTED");
        checkouts.addAll(checkoutService.getCheckoutsByStatus("PROCESSING"));
        return ResponseEntity.ok(checkouts);
    }

    @PutMapping("/{checkoutId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Checkout> updateCheckoutStatus(
            @PathVariable UUID checkoutId,
            @RequestParam String status) {
        Checkout checkout = checkoutService.updateStatus(checkoutId, status);
        return ResponseEntity.ok(checkout);
    }
}