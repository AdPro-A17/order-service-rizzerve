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

    private final RestTemplate restTemplate;

    @Value("${table.service.url}")
    private String tableServiceUrl;

    /**
     * Check if a table is available for seating
     */
    public boolean isTableAvailable(int tableNumber) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(tableServiceUrl)
                    .path("/api/table/check-availability")
                    .queryParam("tableNumber", tableNumber)
                    .toUriString();

            TableAvailabilityResponse response = restTemplate.getForObject(url, TableAvailabilityResponse.class);
            
            log.info("Table {} availability check: {}", tableNumber, response != null ? response.isAvailable() : false);
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

            String url = UriComponentsBuilder.fromHttpUrl(tableServiceUrl)
                    .path("/api/table/update-status")
                    .queryParam("tableNumber", tableNumber)
                    .queryParam("status", "TERPAKAI")
                    .queryParam("activeOrderId", orderId.toString())
                    .queryParam("activeOrderStatus", "PENDING")
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
            String url = UriComponentsBuilder.fromHttpUrl(tableServiceUrl)
                    .path("/api/table/update-status")
                    .queryParam("tableNumber", tableNumber)
                    .queryParam("status", "TERSEDIA")
                    .queryParam("activeOrderId", "")
                    .queryParam("activeOrderStatus", "")
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
            String url = UriComponentsBuilder.fromHttpUrl(tableServiceUrl)
                    .path("/api/table/nomor/" + tableNumber)
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
            String url = UriComponentsBuilder.fromHttpUrl(tableServiceUrl)
                    .path("/api/table/update-status")
                    .queryParam("tableNumber", tableNumber)
                    .queryParam("status", "TERPAKAI")
                    .queryParam("activeOrderId", orderId.toString())
                    .queryParam("activeOrderStatus", orderStatus)
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