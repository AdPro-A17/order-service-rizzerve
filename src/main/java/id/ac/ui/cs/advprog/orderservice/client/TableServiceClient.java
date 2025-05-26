package id.ac.ui.cs.advprog.orderservice.client;

import id.ac.ui.cs.advprog.orderservice.exception.TableNotAvailableException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableServiceClient {

    private static final String TABLE_NUMBER_PARAM = "tableNumber";
    private static final String STATUS_PARAM = "status";
    private static final String ACTIVE_ORDER_ID_PARAM = "activeOrderId";
    private static final String ACTIVE_ORDER_STATUS_PARAM = "activeOrderStatus";
    private static final String STATUS_TERPAKAI = "TERPAKAI";
    private static final String STATUS_TERSEDIA = "TERSEDIA";
    private static final String STATUS_PENDING = "PENDING";

    private final RestTemplate restTemplate;

    @Value("${table.service.url}")
    private String tableServiceUrl;
    
    @Value("${table.service.update-status-path:/api/table/update-status}")
    private String updateStatusPath;
    
    @Value("${table.service.check-availability-path:/api/table/check-availability}")
    private String checkAvailabilityPath;
    
    @Value("${table.service.table-by-number-path:/api/table/nomor/}")
    private String tableByNumberPath;

    /**
     * Check if a table is available for seating
     */
    public boolean isTableAvailable(int tableNumber) {
        try {
            String url = UriComponentsBuilder.fromUriString(tableServiceUrl)
                    .path(checkAvailabilityPath)
                    .queryParam(TABLE_NUMBER_PARAM, tableNumber)
                    .toUriString();

            TableAvailabilityResponse response = restTemplate.getForObject(url, TableAvailabilityResponse.class);
            
            log.info("Table {} availability check: {}", tableNumber, response != null && response.isAvailable());
            return response != null && response.isAvailable();
        } catch (Exception e) {
            log.error("Error checking table availability for table {}: {}", tableNumber, e.getMessage());
            return false; // Assume not available if service is down
        }
    }

    /**
     * Reserve a table by marking it as occupied (TERPAKAI)
     */
    public void reserveTable(int tableNumber, UUID orderId) {
        try {
            if (!isTableAvailable(tableNumber)) {
                throw new TableNotAvailableException("Table " + tableNumber + " is not available");
            }

            String url = UriComponentsBuilder.fromUriString(tableServiceUrl)
                    .path(updateStatusPath)
                    .queryParam(TABLE_NUMBER_PARAM, tableNumber)
                    .queryParam(STATUS_PARAM, STATUS_TERPAKAI)
                    .queryParam(ACTIVE_ORDER_ID_PARAM, orderId.toString())
                    .queryParam(ACTIVE_ORDER_STATUS_PARAM, STATUS_PENDING)
                    .toUriString();

            restTemplate.put(url, null);
            log.info("Reserved table {} for order {}", tableNumber, orderId);
        } catch (Exception e) {
            log.error("Error reserving table {} for order {}: {}", tableNumber, orderId, e.getMessage());
            throw new TableNotAvailableException("Failed to reserve table " + tableNumber);
        }
    }

    /**
     * Release a table by marking it as available (TERSEDIA)
     */
    public void releaseTable(int tableNumber, UUID orderId) {
        try {
            String url = UriComponentsBuilder.fromUriString(tableServiceUrl)
                    .path(updateStatusPath)
                    .queryParam(TABLE_NUMBER_PARAM, tableNumber)
                    .queryParam(STATUS_PARAM, STATUS_TERSEDIA)
                    .queryParam(ACTIVE_ORDER_ID_PARAM, "")
                    .queryParam(ACTIVE_ORDER_STATUS_PARAM, "")
                    .toUriString();

            restTemplate.put(url, null);
            log.info("Released table {} from order {}", tableNumber, orderId);
        } catch (Exception e) {
            log.error("Error releasing table {} from order {}: {}", tableNumber, orderId, e.getMessage());
            // Don't throw exception here as order completion shouldn't fail due to table service issues
        }
    }

    /**
     * Get table information by number
     */
    public TableResponse getTableByNumber(int tableNumber) {
        try {
            String url = UriComponentsBuilder.fromUriString(tableServiceUrl)
                    .path(tableByNumberPath + tableNumber)
                    .toUriString();

            GetTableResponse response = restTemplate.getForObject(url, GetTableResponse.class);
            return response != null ? response.getData() : null;
        } catch (Exception e) {
            log.error("Error getting table {}: {}", tableNumber, e.getMessage());
            return null;
        }
    }

    public void updateTableStatus(int tableNumber, UUID orderId, String orderStatus) {
        try {
            String url = UriComponentsBuilder.fromUriString(tableServiceUrl)
                    .path(updateStatusPath)
                    .queryParam(TABLE_NUMBER_PARAM, tableNumber)
                    .queryParam(STATUS_PARAM, STATUS_TERPAKAI)
                    .queryParam(ACTIVE_ORDER_ID_PARAM, orderId.toString())
                    .queryParam(ACTIVE_ORDER_STATUS_PARAM, orderStatus)
                    .toUriString();

            restTemplate.put(url, null);
            log.info("Updated table {} status for order {} to {}", tableNumber, orderId, orderStatus);
        } catch (Exception e) {
            log.error("Error updating table {} status for order {}: {}", tableNumber, orderId, e.getMessage());
            // Don't throw exception here as order confirmation shouldn't fail due to table service issues
        }
    }

    // Response DTOs
    @Data
    public static class TableAvailabilityResponse {
        private boolean available;
        private String status;
    }

    @Data
    public static class GetTableResponse {
        private String message;
        private TableResponse data;
    }

    @Data
    public static class TableResponse {
        private UUID id;
        private int nomorMeja;
        private String status;
        private UUID activeOrderId;
        private String activeOrderStatus;
        private Double activeOrderTotalPrice;
    }
} 