package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.dto.*;
import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.orderservice.exception.InvalidOrderStatusForCheckoutException;
import id.ac.ui.cs.advprog.orderservice.exception.CouponApplicationException; // Import the new exception
import org.springframework.http.HttpStatus;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;

import java.util.List;

@RestController
@RequestMapping("/api/checkouts")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public ResponseEntity<Object> createCheckout(@RequestBody CheckoutRequest request) {
        try {
            Checkout checkout = checkoutService.createCheckout(request);
            return ResponseEntity.ok(mapToCheckoutResponse(checkout));
        } catch (InvalidOrderStatusForCheckoutException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pesanan sudah dibayar");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (CouponApplicationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<CheckoutResponse>> getCheckoutsByTable(@PathVariable int tableNumber) {
        try {
            List<Checkout> checkouts = checkoutService.getCheckoutsByTable(tableNumber);
            return ResponseEntity.ok(checkouts.stream()
                    .map(this::mapToCheckoutResponse)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private CheckoutResponse mapToCheckoutResponse(Checkout checkout) {
        return CheckoutResponse.builder()
                .checkoutId(checkout.getId())
                .tableNumber(checkout.getTableNumber())
                .items(checkout.getItems().stream()
                        .map(this::mapToCheckoutItemResponse)
                        .toList())
                .totalPrice(checkout.getTotalPrice())
                .couponCode(checkout.getCouponCode())
                .discountAmount(checkout.getDiscountAmount())
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