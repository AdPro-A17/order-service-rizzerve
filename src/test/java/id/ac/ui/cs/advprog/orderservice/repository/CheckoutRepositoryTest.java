package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutRepositoryTest {

    private CheckoutRepository checkoutRepository;
    private Checkout checkout;
    private UUID checkoutId;

    @BeforeEach
    void setUp() {
        checkoutRepository = new CheckoutRepository();
        checkout = new Checkout();
        checkoutId = UUID.randomUUID();
        checkout.setId(checkoutId);
        checkout.setTableNumber("A1");

        OrderItem item = new OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        item.setMenuItemName("Nasi Goreng");
        item.setQuantity(2);
        item.setPrice(25000.0);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(item);
        checkout.setOrderItems(orderItems);

        checkout.setTotalPrice(50000.0);
        checkout.setFinalPrice(50000.0);
    }

    @Test
    void save_ShouldSaveAndReturnCheckout() {
        Checkout savedCheckout = checkoutRepository.save(checkout);
        assertNotNull(savedCheckout);
        assertEquals(checkout.getId(), savedCheckout.getId());
        assertEquals(checkout.getTableNumber(), savedCheckout.getTableNumber());
        assertEquals(checkout.getTotalPrice(), savedCheckout.getTotalPrice());
        assertEquals(checkout.getFinalPrice(), savedCheckout.getFinalPrice());
    }

    @Test
    void save_WithNullId_ShouldGenerateIdAndSaveCheckout() {
        checkout.setId(null);
        Checkout savedCheckout = checkoutRepository.save(checkout);
        assertNotNull(savedCheckout);
        assertNotNull(savedCheckout.getId());
        assertEquals(checkout.getTableNumber(), savedCheckout.getTableNumber());
    }

    @Test
    void findById_WithExistingId_ShouldReturnCheckout() {
        checkoutRepository.save(checkout);
        Checkout foundCheckout = checkoutRepository.findById(checkoutId);
        assertNotNull(foundCheckout);
        assertEquals(checkoutId, foundCheckout.getId());
        assertEquals(checkout.getTableNumber(), foundCheckout.getTableNumber());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnNull() {
        UUID nonExistingId = UUID.randomUUID();
        Checkout foundCheckout = checkoutRepository.findById(nonExistingId);
        assertNull(foundCheckout);
    }

    @Test
    void findAll_WithNoCheckouts_ShouldReturnEmptyList() {
        List<Checkout> checkouts = checkoutRepository.findAll();
        assertNotNull(checkouts);
        assertTrue(checkouts.isEmpty());
    }

    @Test
    void findAll_WithCheckouts_ShouldReturnAllCheckouts() {
        checkoutRepository.save(checkout);
        Checkout checkout2 = new Checkout();
        checkout2.setId(UUID.randomUUID());
        checkout2.setTableNumber("B2");
        checkoutRepository.save(checkout2);
        List<Checkout> checkouts = checkoutRepository.findAll();
        assertNotNull(checkouts);
        assertEquals(2, checkouts.size());
        assertTrue(checkouts.stream().anyMatch(c -> c.getId().equals(checkout.getId())));
        assertTrue(checkouts.stream().anyMatch(c -> c.getId().equals(checkout2.getId())));
    }

    @Test
    void delete_WithExistingId_ShouldRemoveCheckout() {
        checkoutRepository.save(checkout);
        checkoutRepository.delete(checkoutId);
        Checkout deletedCheckout = checkoutRepository.findById(checkoutId);
        assertNull(deletedCheckout);
    }

    @Test
    void delete_WithNonExistingId_ShouldNotThrowException() {
        UUID nonExistingId = UUID.randomUUID();
        assertDoesNotThrow(() -> checkoutRepository.delete(nonExistingId));
    }
}
