//package id.ac.ui.cs.advprog.orderservice.repository;
//
//import id.ac.ui.cs.advprog.orderservice.model.Checkout;
//import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//class CheckoutRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private CheckoutRepository checkoutRepository;
//
//    private Checkout checkout1;
//    private Checkout checkout2;
//    private Checkout checkout3;
//
//    @BeforeEach
//    void setUp() {
//        // Create OrderItems
//        OrderItem item1 = new OrderItem();
//        item1.setMenuItemId(UUID.randomUUID());
//        item1.setMenuItemName("Pizza");
//        item1.setQuantity(2);
//        item1.setPrice(15.99);
//        item1.setSubtotal(31.98);
//
//        OrderItem item2 = new OrderItem();
//        item2.setMenuItemId(UUID.randomUUID());
//        item2.setMenuItemName("Burger");
//        item2.setQuantity(1);
//        item2.setPrice(12.50);
//        item2.setSubtotal(12.50);
//
//        OrderItem item3 = new OrderItem();
//        item3.setMenuItemId(UUID.randomUUID());
//        item3.setMenuItemName("Pasta");
//        item3.setQuantity(3);
//        item3.setPrice(18.00);
//        item3.setSubtotal(54.00);
//
//        // Create checkouts for table 1
//        checkout1 = new Checkout();
//        checkout1.setTableNumber(1);
//        checkout1.setItems(List.of(item1, item2));
//        checkout1.setTotalPrice(44.48);
//        checkout1.setCouponCode("SAVE10");
//        checkout1.setDiscountAmount(5.00);
//
//        checkout2 = new Checkout();
//        checkout2.setTableNumber(1);
//        checkout2.setItems(List.of(item3));
//        checkout2.setTotalPrice(54.00);
//        checkout2.setCouponCode(null);
//        checkout2.setDiscountAmount(0.0);
//
//        // Create checkout for table 2
//        checkout3 = new Checkout();
//        checkout3.setTableNumber(2);
//        checkout3.setItems(List.of(item1));
//        checkout3.setTotalPrice(31.98);
//        checkout3.setCouponCode("DISCOUNT20");
//        checkout3.setDiscountAmount(6.40);
//
//        // Persist checkouts
//        entityManager.persistAndFlush(checkout1);
//        entityManager.persistAndFlush(checkout2);
//        entityManager.persistAndFlush(checkout3);
//    }
//
//    @Test
//    void testFindByTableNumber_WhenTableHasCheckouts_ShouldReturnCheckouts() {
//        // When
//        List<Checkout> checkouts = checkoutRepository.findByTableNumber(1);
//
//        // Then
//        assertThat(checkouts).hasSize(2);
//        assertThat(checkouts).extracting(Checkout::getTableNumber).containsOnly(1);
//        assertThat(checkouts).extracting(Checkout::getId)
//                .containsExactlyInAnyOrder(checkout1.getId(), checkout2.getId());
//    }
//
//    @Test
//    void testFindByTableNumber_WhenTableHasOneCheckout_ShouldReturnSingleCheckout() {
//        // When
//        List<Checkout> checkouts = checkoutRepository.findByTableNumber(2);
//
//        // Then
//        assertThat(checkouts).hasSize(1);
//        assertThat(checkouts.get(0).getTableNumber()).isEqualTo(2);
//        assertThat(checkouts.get(0).getId()).isEqualTo(checkout3.getId());
//        assertThat(checkouts.get(0).getCouponCode()).isEqualTo("DISCOUNT20");
//        assertThat(checkouts.get(0).getDiscountAmount()).isEqualTo(6.40);
//    }
//
//    @Test
//    void testFindByTableNumber_WhenTableHasNoCheckouts_ShouldReturnEmptyList() {
//        // When
//        List<Checkout> checkouts = checkoutRepository.findByTableNumber(999);
//
//        // Then
//        assertThat(checkouts).isEmpty();
//    }
//
//    @Test
//    void testFindByTableNumber_WhenTableNumberIsZero_ShouldReturnEmptyList() {
//        // When
//        List<Checkout> checkouts = checkoutRepository.findByTableNumber(0);
//
//        // Then
//        assertThat(checkouts).isEmpty();
//    }
//
//    @Test
//    void testFindByTableNumber_WhenTableNumberIsNegative_ShouldReturnEmptyList() {
//        // When
//        List<Checkout> checkouts = checkoutRepository.findByTableNumber(-1);
//
//        // Then
//        assertThat(checkouts).isEmpty();
//    }
//
//    @Test
//    void testSave_WhenSavingNewCheckout_ShouldPersistCheckout() {
//        // Given
//        OrderItem newItem = new OrderItem();
//        newItem.setMenuItemId(UUID.randomUUID());
//        newItem.setMenuItemName("Salad");
//        newItem.setQuantity(1);
//        newItem.setPrice(9.99);
//        newItem.setSubtotal(9.99);
//
//        Checkout newCheckout = new Checkout();
//        newCheckout.setTableNumber(3);
//        newCheckout.setItems(List.of(newItem));
//        newCheckout.setTotalPrice(9.99);
//        newCheckout.setCouponCode(null);
//        newCheckout.setDiscountAmount(0.0);
//
//        // When
//        Checkout savedCheckout = checkoutRepository.save(newCheckout);
//
//        // Then
//        assertThat(savedCheckout.getId()).isNotNull();
//        assertThat(savedCheckout.getTableNumber()).isEqualTo(3);
//        assertThat(savedCheckout.getTotalPrice()).isEqualTo(9.99);
//        assertThat(savedCheckout.getItems()).hasSize(1);
//        assertThat(savedCheckout.getItems().get(0).getMenuItemName()).isEqualTo("Salad");
//
//        // Verify persistence
//        Optional<Checkout> foundCheckout = checkoutRepository.findById(savedCheckout.getId());
//        assertThat(foundCheckout).isPresent();
//        assertThat(foundCheckout.get().getTableNumber()).isEqualTo(3);
//    }
//
//    @Test
//    void testSave_WhenSavingCheckoutWithCoupon_ShouldPersistWithCouponDetails() {
//        // Given
//        OrderItem item = new OrderItem();
//        item.setMenuItemId(UUID.randomUUID());
//        item.setMenuItemName("Steak");
//        item.setQuantity(1);
//        item.setPrice(25.00);
//        item.setSubtotal(25.00);
//
//        Checkout checkout = new Checkout();
//        checkout.setTableNumber(4);
//        checkout.setItems(List.of(item));
//        checkout.setTotalPrice(20.00);
//        checkout.setCouponCode("PREMIUM15");
//        checkout.setDiscountAmount(5.00);
//
//        // When
//        Checkout savedCheckout = checkoutRepository.save(checkout);
//
//        // Then
//        assertThat(savedCheckout.getId()).isNotNull();
//        assertThat(savedCheckout.getCouponCode()).isEqualTo("PREMIUM15");
//        assertThat(savedCheckout.getDiscountAmount()).isEqualTo(5.00);
//        assertThat(savedCheckout.getTotalPrice()).isEqualTo(20.00);
//
//        // Verify persistence
//        Optional<Checkout> foundCheckout = checkoutRepository.findById(savedCheckout.getId());
//        assertThat(foundCheckout).isPresent();
//        assertThat(foundCheckout.get().getCouponCode()).isEqualTo("PREMIUM15");
//        assertThat(foundCheckout.get().getDiscountAmount()).isEqualTo(5.00);
//    }
//
//    @Test
//    void testFindById_WhenCheckoutExists_ShouldReturnCheckout() {
//        // When
//        Optional<Checkout> foundCheckout = checkoutRepository.findById(checkout1.getId());
//
//        // Then
//        assertThat(foundCheckout).isPresent();
//        assertThat(foundCheckout.get().getTableNumber()).isEqualTo(1);
//        assertThat(foundCheckout.get().getCouponCode()).isEqualTo("SAVE10");
//        assertThat(foundCheckout.get().getItems()).hasSize(2);
//        assertThat(foundCheckout.get().getCreatedAt()).isNotNull();
//    }
//
//    @Test
//    void testFindById_WhenCheckoutDoesNotExist_ShouldReturnEmpty() {
//        // When
//        Optional<Checkout> foundCheckout = checkoutRepository.findById(UUID.randomUUID());
//
//        // Then
//        assertThat(foundCheckout).isEmpty();
//    }
//
//    @Test
//    void testFindAll_ShouldReturnAllCheckouts() {
//        // When
//        List<Checkout> allCheckouts = checkoutRepository.findAll();
//
//        // Then
//        assertThat(allCheckouts).hasSize(3);
//        assertThat(allCheckouts).extracting(Checkout::getId)
//                .containsExactlyInAnyOrder(checkout1.getId(), checkout2.getId(), checkout3.getId());
//    }
//
//    @Test
//    void testDelete_WhenDeletingExistingCheckout_ShouldRemoveFromDatabase() {
//        // Given
//        UUID checkoutId = checkout1.getId();
//
//        // When
//        checkoutRepository.delete(checkout1);
//
//        // Then
//        Optional<Checkout> deletedCheckout = checkoutRepository.findById(checkoutId);
//        assertThat(deletedCheckout).isEmpty();
//
//        // Verify other checkouts still exist
//        List<Checkout> remainingCheckouts = checkoutRepository.findAll();
//        assertThat(remainingCheckouts).hasSize(2);
//        assertThat(remainingCheckouts).extracting(Checkout::getId)
//                .containsExactlyInAnyOrder(checkout2.getId(), checkout3.getId());
//    }
//
//    @Test
//    void testSave_WhenUpdatingExistingCheckout_ShouldUpdateCheckout() {
//        // Given
//        checkout1.setTotalPrice(50.00);
//        checkout1.setDiscountAmount(10.00);
//        checkout1.setCouponCode("NEWCODE");
//
//        // When
//        Checkout updatedCheckout = checkoutRepository.save(checkout1);
//
//        // Then
//        assertThat(updatedCheckout.getId()).isEqualTo(checkout1.getId());
//        assertThat(updatedCheckout.getTotalPrice()).isEqualTo(50.00);
//        assertThat(updatedCheckout.getDiscountAmount()).isEqualTo(10.00);
//        assertThat(updatedCheckout.getCouponCode()).isEqualTo("NEWCODE");
//
//        // Verify persistence
//        Optional<Checkout> foundCheckout = checkoutRepository.findById(checkout1.getId());
//        assertThat(foundCheckout).isPresent();
//        assertThat(foundCheckout.get().getTotalPrice()).isEqualTo(50.00);
//        assertThat(foundCheckout.get().getCouponCode()).isEqualTo("NEWCODE");
//    }
//}