package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.processCheckout(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout/test")
    public ResponseEntity<String> testCheckout() {
        return ResponseEntity.ok("Checkout endpoint is working!");
    }
}
