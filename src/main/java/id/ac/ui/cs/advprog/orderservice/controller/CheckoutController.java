package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.*;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/checkouts")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public ResponseEntity<?> createCheckout(@RequestBody CheckoutRequest request) {
        try {
            Checkout checkout = checkoutService.createCheckout(request);
            return ResponseEntity.ok(mapToCheckoutResponse(checkout));
        } catch (InvalidOrderStatusForCheckoutException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pesanan sudah dibayar");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<CheckoutResponse>> getCheckoutsByTable(@PathVariable int tableNumber) {
        List<Checkout> checkouts = checkoutService.getCheckoutsByTable(tableNumber);
        return ResponseEntity.ok(checkouts.stream()
                .map(this::mapToCheckoutResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CheckoutResponse>> getAllCheckouts() {
        List<Checkout> checkouts = checkoutService.getCheckoutsByStatus("SUBMITTED");
        checkouts.addAll(checkoutService.getCheckoutsByStatus("PROCESSING"));
        return ResponseEntity.ok(checkouts.stream()
                .map(this::mapToCheckoutResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{checkoutId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CheckoutResponse> updateCheckoutStatus(
            @PathVariable UUID checkoutId,
            @RequestParam String status) {
        Checkout checkout = checkoutService.updateStatus(checkoutId, status);
        return ResponseEntity.ok(mapToCheckoutResponse(checkout));
    }

    private CheckoutResponse mapToCheckoutResponse(Checkout checkout) {
        return CheckoutResponse.builder()
                .checkoutId(checkout.getId())
                .tableNumber(checkout.getTableNumber())
                .items(checkout.getItems().stream()
                        .map(this::mapToCheckoutItemResponse)
                        .collect(Collectors.toList()))
                .totalPrice(checkout.getTotalPrice())
                .couponCode(checkout.getCouponCode())
                .discountAmount(checkout.getDiscountAmount())
                .finalPrice(checkout.getFinalPrice())
                .status(checkout.getStatus())
                .orderStatus("PROCESSING")
                .build();
    }

    private CheckoutItemResponse mapToCheckoutItemResponse(OrderItem item) {
        return CheckoutItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItemId())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}