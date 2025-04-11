package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Configure H2, Hibernate, Spring Data, etc.
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository; // Spring Boot will inject the repo implementation

    private Order order1;
    private Order order2;
    private UUID order1Id;

    @BeforeEach
    void setUp() {
        // Ensure Order model exists with necessary annotations (@Entity, @Id, etc.)
        order1 = new Order("T1");
        // order1.setState(new NewOrderState(order1)); // Set initial state if required by model
        // Add items if necessary for testing relationships

        order2 = new Order("T2");
        // order2.setState(new NewOrderState(order2));

        // Need to persist first to get generated ID
        order1 = entityManager.persistFlushFind(order1);
        order1Id = order1.getId();
        entityManager.persist(order2);
        entityManager.flush(); // Flush changes to H2 database

        fail("Order model needs @Entity, @Id, potentially @OneToMany for items, and OrderRepository interface extending JpaRepository.");
    }

    @Test
    void whenFindById_thenReturnOrder() {
        Optional<Order> found = orderRepository.findById(order1Id);

        assertTrue(found.isPresent());
        assertEquals(order1.getTableNumber(), found.get().getTableNumber());
    }

    @Test
    void whenFindById_withNonExistentId_thenReturnEmpty() {
        Optional<Order> found = orderRepository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void whenFindAll_thenReturnAllOrders() {
        List<Order> orders = orderRepository.findAll();

        assertNotNull(orders);
        assertEquals(2, orders.size()); // order1 and order2 were persisted
        // Add more specific assertions if needed, e.g., check table numbers
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order1Id)));
        assertTrue(orders.stream().anyMatch(o -> o.getTableNumber().equals("T2")));
    }

    @Test
    void whenSave_thenPersistOrder() {
        Order newOrder = new Order("T3");
        // newOrder.setState(new NewOrderState(newOrder));
        Order savedOrder = orderRepository.save(newOrder);

        assertNotNull(savedOrder.getId());
        assertEquals("T3", savedOrder.getTableNumber());

        // Verify it's actually in the DB
        Order foundInDb = entityManager.find(Order.class, savedOrder.getId());
        assertNotNull(foundInDb);
        assertEquals("T3", foundInDb.getTableNumber());
    }

     @Test
    void testDeleteOrder() {
        assertTrue(orderRepository.findById(order1Id).isPresent()); // Ensure it exists first

        orderRepository.deleteById(order1Id);
        entityManager.flush(); // Ensure delete is processed

        assertFalse(orderRepository.findById(order1Id).isPresent());
    }

    // Add tests for any custom query methods defined in OrderRepository
    // Example: findByStatus, findByTableNumber
    // @Test
    // void whenFindByTableNumber_thenReturnCorrectOrders() {
    //     List<Order> found = orderRepository.findByTableNumber("T1");
    //     assertEquals(1, found.size());
    //     assertEquals(order1Id, found.get(0).getId());
    //     fail("findByTableNumber method needs to be defined in OrderRepository.");
    // }
} 