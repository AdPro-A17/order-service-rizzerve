package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.model.Order;
import id.ac.ui.cs.advprog.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderTableManagementServiceImpl implements OrderTableManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderTableManagementServiceImpl.class);
    private final OrderRepository orderRepository;

    @Autowired
    public OrderTableManagementServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void handleTableDeleted(UUID mejaId, Integer nomorMeja) {
        String tableNumStr = String.valueOf(nomorMeja);
        List<Order> ordersToDelete = orderRepository.findByTableNumberAndStatusStringNotIn(tableNumStr, List.of("COMPLETED", "CANCELLED"));

        if (!ordersToDelete.isEmpty()) {
            LOGGER.info("Deleting {} active orders associated with deleted table number: {}", ordersToDelete.size(), tableNumStr);
            orderRepository.deleteAll(ordersToDelete);
        } else {
            LOGGER.info("No active orders found for deleted table number: {}", tableNumStr);
        }
    }

    @Override
    @Transactional
    public void handleTableNumberUpdated(UUID mejaId, String oldTableNumber, String newTableNumber) {
        List<Order> ordersToUpdate = orderRepository.findByTableNumberAndStatusStringNotIn(oldTableNumber, List.of("COMPLETED", "CANCELLED"));

        if (!ordersToUpdate.isEmpty()) {
            LOGGER.info("Updating table number for {} active orders from {} to {}", ordersToUpdate.size(), oldTableNumber, newTableNumber);
            for (Order order : ordersToUpdate) {
                order.setTableNumber(newTableNumber);
            }
            orderRepository.saveAll(ordersToUpdate);
        } else {
            LOGGER.info("No active orders found for table number update from {} to {}", oldTableNumber, newTableNumber);
        }
    }

    @Override
    @Transactional
    public void handleTableBecameAvailable(UUID mejaId, String tableNumber) {
        LOGGER.info("Table {} (ID: {}) is now available. No specific order modification implemented for this event in OrderService.", tableNumber, mejaId);
    }
}