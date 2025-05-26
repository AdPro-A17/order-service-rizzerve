package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.model.OrderItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order order1;
    private Order order2;
    private UUID order1Id;
    private UUID order2Id; // Add ID for order2
    private UUID item1Id;

    @BeforeEach
    void setUp() {
        // Create new entities without manual ID assignment
        order1 = new Order("T1");
        OrderItem item1 = new OrderItem(order1, UUID.randomUUID(), "Item A", 1, 10.0);
        order1.addItem(item1);

        order2 = new Order("T2");
        OrderItem item2 = new OrderItem(order2, UUID.randomUUID(), "Item B", 2, 5.0);
        order2.addItem(item2);

        // Use persistAndFlush to save and get managed entities with generated IDs
        order1 = entityManager.persistAndFlush(order1);
        order1Id = order1.getId(); // Get the generated ID
        item1Id = order1.getItems().get(0).getId(); // Get generated item ID

        order2 = entityManager.persistAndFlush(order2);
        order2Id = order2.getId(); // Get the generated ID
    }

     @AfterEach
    void tearDown() {
        // Clean up the database after each test
        entityManager.clear();
        orderRepository.deleteAll(); // Ensure clean slate
        entityManager.flush();
    }


    @Test
    void whenFindById_thenReturnOrderWithItemsAndState() {
        // Act: Fetch directly using the repository
        Optional<Order> foundOptional = orderRepository.findById(order1Id);

        // Assert
        assertTrue(foundOptional.isPresent());
        Order found = foundOptional.get();
        entityManager.detach(found); // Detach before accessing lazy/transient fields outside transaction

        assertEquals(order1Id, found.getId());
        assertEquals("T1", found.getTableNumber());
        assertEquals(1, found.getItems().size()); // Items should be loaded (EAGER fetch)
        assertEquals(item1Id, found.getItems().get(0).getId());
        assertEquals("Item A", found.getItems().get(0).getMenuItemName());
        assertEquals(10.0, found.getTotalPrice());
        assertEquals("NEW", found.getStatusString()); // Check persisted status string
        
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
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order1Id)));
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order2Id)));
    }

    @Test
    void whenSave_thenPersistOrderAndItems() {
        // Arrange
        Order newOrder = new Order("T3");
        OrderItem newItem = new OrderItem(newOrder, UUID.randomUUID(), "Item C", 3, 2.0);
        newOrder.addItem(newItem);

        // Act
        Order savedOrder = orderRepository.save(newOrder);
        UUID newOrderId = savedOrder.getId();
        entityManager.flush(); // Ensure saved to DB
        entityManager.clear(); // Clear context to force reload

        // Assert
        assertNotNull(newOrderId);

        // Verify it's actually in the DB via findById
        Order foundInDb = orderRepository.findById(newOrderId).orElseThrow();
        assertNotNull(foundInDb);
        assertEquals(newOrderId, foundInDb.getId());
        assertEquals("T3", foundInDb.getTableNumber());
        assertEquals(6.0, foundInDb.getTotalPrice());
        assertEquals("NEW", foundInDb.getStatusString());
        assertEquals(1, foundInDb.getItems().size());
        assertEquals("Item C", foundInDb.getItems().get(0).getMenuItemName());
    }

     @Test
    void testUpdateOrderState() {
        // Arrange: Fetch, modify state (only statusString matters for repo test), save
        Order foundOrder = orderRepository.findById(order1Id).orElseThrow();
        // Directly modify the persisted status string for repo test
        foundOrder.setStatusString("PROCESSING");

        // Act
        orderRepository.save(foundOrder);
        entityManager.flush();
        entityManager.clear();

        // Assert: Reload and check the persisted string
        Order reloadedOrder = orderRepository.findById(order1Id).orElseThrow();
        assertEquals("PROCESSING", reloadedOrder.getStatusString());
    }

     @Test
    void testDeleteOrderCascadesToItems() {
        // Arrange: Ensure order and potentially item exist
        assertTrue(orderRepository.findById(order1Id).isPresent());
        // Need item ID that was persisted
        OrderItem itemInOrder1 = entityManager.find(Order.class, order1Id).getItems().get(0);
        UUID persistedItem1Id = itemInOrder1.getId();
        assertNotNull(entityManager.find(OrderItem.class, persistedItem1Id));

        // Act
        orderRepository.deleteById(order1Id);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertFalse(orderRepository.findById(order1Id).isPresent());
        // Verify item is also deleted due to CascadeType.ALL and orphanRemoval=true
        assertNull(entityManager.find(OrderItem.class, persistedItem1Id)); // Item should be gone
    }

    
} 