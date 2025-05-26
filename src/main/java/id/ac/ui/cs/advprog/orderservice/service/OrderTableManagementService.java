package id.ac.ui.cs.advprog.orderservice.service;

import java.util.UUID;

public interface OrderTableManagementService {
    void handleTableDeleted(UUID mejaId, Integer nomorMeja);
    void handleTableNumberUpdated(UUID mejaId, String oldTableNumber, String newTableNumber);
    void handleTableBecameAvailable(UUID mejaId, String tableNumber);
}