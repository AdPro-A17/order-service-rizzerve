package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableManagementServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableManagementServiceImpl orderTableManagementService;

    @Test
    void handleTableDeletedAndOrdersExistThenDelete() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 101;
        String tableNumStr = String.valueOf(nomorMeja);

        Order order1 = new Order(tableNumStr);
        order1.setId(UUID.randomUUID());
        Order order2 = new Order(tableNumStr);
        order2.setId(UUID.randomUUID());
        List<Order> mockOrders = List.of(order1, order2);

        when(orderRepository.findByTableNumberAndStatusStringNotIn(eq(tableNumStr), anyList())).thenReturn(mockOrders);

        orderTableManagementService.handleTableDeleted(mejaId, nomorMeja);

        verify(orderRepository).findByTableNumberAndStatusStringNotIn(tableNumStr, List.of("COMPLETED", "CANCELLED"));
        verify(orderRepository).deleteAll(mockOrders);
    }

    @Test
    void handleTableDeletedAndNoOrdersExistThenDoNothing() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 102;
        String tableNumStr = String.valueOf(nomorMeja);

        when(orderRepository.findByTableNumberAndStatusStringNotIn(eq(tableNumStr), anyList())).thenReturn(new ArrayList<>());

        orderTableManagementService.handleTableDeleted(mejaId, nomorMeja);

        verify(orderRepository).findByTableNumberAndStatusStringNotIn(tableNumStr, List.of("COMPLETED", "CANCELLED"));
        verify(orderRepository, never()).deleteAll(anyList());
    }

    @Test
    void handleTableNumberUpdatedAndOrdersExistThenUpdatesAndSavesOrders() {
        UUID mejaId = UUID.randomUUID();
        String oldTableNumber = "103";
        String newTableNumber = "104";

        Order order1 = new Order(oldTableNumber);
        order1.setId(UUID.randomUUID());
        List<Order> mockOrders = List.of(order1);

        when(orderRepository.findByTableNumberAndStatusStringNotIn(eq(oldTableNumber), anyList())).thenReturn(mockOrders);

        orderTableManagementService.handleTableNumberUpdated(mejaId, oldTableNumber, newTableNumber);

        verify(orderRepository).findByTableNumberAndStatusStringNotIn(oldTableNumber, List.of("COMPLETED", "CANCELLED"));
        assertEquals(newTableNumber, order1.getTableNumber());
        verify(orderRepository).saveAll(mockOrders);
    }

    @Test
    void handleTableNumberUpdatedAndNoOrdersExistThenDoNothing() {
        UUID mejaId = UUID.randomUUID();
        String oldTableNumber = "105";
        String newTableNumber = "106";

        when(orderRepository.findByTableNumberAndStatusStringNotIn(eq(oldTableNumber), anyList())).thenReturn(new ArrayList<>());

        orderTableManagementService.handleTableNumberUpdated(mejaId, oldTableNumber, newTableNumber);

        verify(orderRepository).findByTableNumberAndStatusStringNotIn(oldTableNumber, List.of("COMPLETED", "CANCELLED"));
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void handleTableBecameAvailableThenLog() {
        UUID mejaId = UUID.randomUUID();
        String tableNumber = "107";

        orderTableManagementService.handleTableBecameAvailable(mejaId, tableNumber);
        
        // Verify no repository interactions occur for this method
        verifyNoInteractions(orderRepository);
    }
}