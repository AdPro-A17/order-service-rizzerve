package id.ac.ui.cs.advprog.orderservice.client;

import id.ac.ui.cs.advprog.orderservice.exception.TableNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TableServiceClient tableServiceClient;

    private final String tableServiceUrl = "http://localhost:8085";
    private final int tableNumber = 1;
    private final UUID orderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tableServiceClient, "tableServiceUrl", tableServiceUrl);
    }

    @Test
    void testIsTableAvailable_WhenTableIsAvailable_ShouldReturnTrue() {
        // Arrange
        TableServiceClient.TableAvailabilityResponse response = new TableServiceClient.TableAvailabilityResponse();
        response.setAvailable(true);
        response.setStatus("TERSEDIA");

        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class)))
                .thenReturn(response);

        // Act
        boolean result = tableServiceClient.isTableAvailable(tableNumber);

        // Assert
        assertTrue(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class));
    }

    @Test
    void testIsTableAvailable_WhenTableIsNotAvailable_ShouldReturnFalse() {
        // Arrange
        TableServiceClient.TableAvailabilityResponse response = new TableServiceClient.TableAvailabilityResponse();
        response.setAvailable(false);
        response.setStatus("TERPAKAI");

        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class)))
                .thenReturn(response);

        // Act
        boolean result = tableServiceClient.isTableAvailable(tableNumber);

        // Assert
        assertFalse(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class));
    }

    @Test
    void testIsTableAvailable_WhenResponseIsNull_ShouldReturnFalse() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class)))
                .thenReturn(null);

        // Act
        boolean result = tableServiceClient.isTableAvailable(tableNumber);

        // Assert
        assertFalse(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class));
    }

    @Test
    void testIsTableAvailable_WhenExceptionOccurs_ShouldReturnFalse() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class)))
                .thenThrow(new RestClientException("Service unavailable"));

        // Act
        boolean result = tableServiceClient.isTableAvailable(tableNumber);

        // Assert
        assertFalse(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class));
    }

    @Test
    void testReserveTable_WhenTableIsAvailable_ShouldSucceed() {
        // Arrange
        TableServiceClient.TableAvailabilityResponse availabilityResponse = new TableServiceClient.TableAvailabilityResponse();
        availabilityResponse.setAvailable(true);

        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class)))
                .thenReturn(availabilityResponse);
        doNothing().when(restTemplate).put(anyString(), isNull());

        // Act & Assert
        assertDoesNotThrow(() -> tableServiceClient.reserveTable(tableNumber, orderId));
        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class));
        verify(restTemplate, times(1)).put(anyString(), isNull());
    }

    

    @Test
    void testReserveTable_WhenUpdateStatusFails_ShouldThrowException() {
        // Arrange
        TableServiceClient.TableAvailabilityResponse availabilityResponse = new TableServiceClient.TableAvailabilityResponse();
        availabilityResponse.setAvailable(true);

        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class)))
                .thenReturn(availabilityResponse);
        doThrow(new RestClientException("Update failed")).when(restTemplate).put(anyString(), isNull());

        // Act & Assert
        TableNotAvailableException exception = assertThrows(
                TableNotAvailableException.class,
                () -> tableServiceClient.reserveTable(tableNumber, orderId)
        );

        assertEquals("Failed to reserve table " + tableNumber, exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.TableAvailabilityResponse.class));
        verify(restTemplate, times(1)).put(anyString(), isNull());
    }

    @Test
    void testReleaseTable_WhenSuccessful_ShouldNotThrowException() {
        // Arrange
        doNothing().when(restTemplate).put(anyString(), isNull());

        // Act & Assert
        assertDoesNotThrow(() -> tableServiceClient.releaseTable(tableNumber, orderId));
        
        verify(restTemplate, times(1)).put(anyString(), isNull());
    }

    @Test
    void testReleaseTable_WhenExceptionOccurs_ShouldNotThrowException() {
        // Arrange
        doThrow(new RestClientException("Release failed")).when(restTemplate).put(anyString(), isNull());

        // Act & Assert
        assertDoesNotThrow(() -> tableServiceClient.releaseTable(tableNumber, orderId));
        
        verify(restTemplate, times(1)).put(anyString(), isNull());
    }

    @Test
    void testGetTableByNumber_WhenTableExists_ShouldReturnTableResponse() {
        // Arrange
        TableServiceClient.TableResponse tableResponse = new TableServiceClient.TableResponse();
        tableResponse.setId(UUID.randomUUID());
        tableResponse.setNomorMeja(tableNumber);
        tableResponse.setStatus("TERSEDIA");

        TableServiceClient.GetTableResponse getTableResponse = new TableServiceClient.GetTableResponse();
        getTableResponse.setMessage("Success");
        getTableResponse.setData(tableResponse);

        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.GetTableResponse.class)))
                .thenReturn(getTableResponse);

        // Act
        TableServiceClient.TableResponse result = tableServiceClient.getTableByNumber(tableNumber);

        // Assert
        assertNotNull(result);
        assertEquals(tableNumber, result.getNomorMeja());
        assertEquals("TERSEDIA", result.getStatus());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.GetTableResponse.class));
    }

    @Test
    void testGetTableByNumber_WhenResponseIsNull_ShouldReturnNull() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.GetTableResponse.class)))
                .thenReturn(null);

        // Act
        TableServiceClient.TableResponse result = tableServiceClient.getTableByNumber(tableNumber);

        // Assert
        assertNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.GetTableResponse.class));
    }

    @Test
    void testGetTableByNumber_WhenExceptionOccurs_ShouldReturnNull() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(TableServiceClient.GetTableResponse.class)))
                .thenThrow(new RestClientException("Service unavailable"));

        // Act
        TableServiceClient.TableResponse result = tableServiceClient.getTableByNumber(tableNumber);

        // Assert
        assertNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TableServiceClient.GetTableResponse.class));
    }

    @Test
    void testTableAvailabilityResponse_SettersAndGetters() {
        // Arrange
        TableServiceClient.TableAvailabilityResponse response = new TableServiceClient.TableAvailabilityResponse();

        // Act
        response.setAvailable(true);
        response.setStatus("TERSEDIA");

        // Assert
        assertTrue(response.isAvailable());
        assertEquals("TERSEDIA", response.getStatus());
    }

    @Test
    void testGetTableResponse_SettersAndGetters() {
        // Arrange
        TableServiceClient.GetTableResponse response = new TableServiceClient.GetTableResponse();
        TableServiceClient.TableResponse tableResponse = new TableServiceClient.TableResponse();

        // Act
        response.setMessage("Success");
        response.setData(tableResponse);

        // Assert
        assertEquals("Success", response.getMessage());
        assertEquals(tableResponse, response.getData());
    }

    @Test
    void testTableResponse_SettersAndGetters() {
        // Arrange
        TableServiceClient.TableResponse response = new TableServiceClient.TableResponse();
        UUID id = UUID.randomUUID();
        UUID activeOrderId = UUID.randomUUID();

        // Act
        response.setId(id);
        response.setNomorMeja(5);
        response.setStatus("TERPAKAI");
        response.setActiveOrderId(activeOrderId);
        response.setActiveOrderStatus("PROCESSING");
        response.setActiveOrderTotalPrice(150000.0);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(5, response.getNomorMeja());
        assertEquals("TERPAKAI", response.getStatus());
        assertEquals(activeOrderId, response.getActiveOrderId());
        assertEquals("PROCESSING", response.getActiveOrderStatus());
        assertEquals(150000.0, response.getActiveOrderTotalPrice());
    }
} 