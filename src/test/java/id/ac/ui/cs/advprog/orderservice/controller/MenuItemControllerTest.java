package id.ac.ui.cs.advprog.orderservice.controller;

import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import id.ac.ui.cs.advprog.orderservice.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class MenuItemControllerTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemController menuItemController;

    private List<MenuItem> menuItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UUID adminId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        MenuItem item1 = MenuItem.create(
                "Nasi Goreng",
                "Nasi goreng special",
                new BigDecimal("25000"),
                true,
                "Main Course",
                4.5,
                adminId
        );

        MenuItem item2 = MenuItem.create(
                "Es Teh",
                "Teh dingin",
                new BigDecimal("8000"),
                true,
                "Beverage",
                4.2,
                adminId
        );

        menuItems = List.of(item1, item2);
    }

    @Test
    void getAllPublicMenuItems_ShouldReturnAvailableMenuItems() {
        when(menuItemRepository.findByAvailableTrue()).thenReturn(menuItems);

        ResponseEntity<List<MenuItem>> responseEntity = menuItemController.getAllPublicMenuItems();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    void getAllCustomerMenuItems_ShouldReturnAvailableMenuItems() {
        when(menuItemRepository.findByAvailableTrue()).thenReturn(menuItems);

        ResponseEntity<List<MenuItem>> responseEntity = menuItemController.getAllCustomerMenuItems();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
    }
}