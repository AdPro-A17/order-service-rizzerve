package id.ac.ui.cs.advprog.orderservice.observer;

import id.ac.ui.cs.advprog.orderservice.dto.MejaEvent;
import id.ac.ui.cs.advprog.orderservice.service.OrderTableManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableEventListenerTest {

    @Mock
    private OrderTableManagementService orderTableManagementService;

    @InjectMocks
    private TableEventListener tableEventListener;

    private MejaEvent createMejaEvent(MejaEvent.Type type, UUID mejaId, Integer nomorMeja, Integer oldNomor, String status) {
        return new MejaEvent(type, mejaId, nomorMeja, oldNomor, status, Instant.now());
    }

    @Test
    void testMejaEventDeleted() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 101;
        MejaEvent event = createMejaEvent(MejaEvent.Type.DELETED, mejaId, nomorMeja, null, "TERPAKAI");

        tableEventListener.handleTableEvent(event);

        verify(orderTableManagementService).handleTableDeleted(eq(mejaId), eq(nomorMeja));
        verify(orderTableManagementService, never()).handleTableNumberUpdated(any(), any(), any());
        verify(orderTableManagementService, never()).handleTableBecameAvailable(any(), any());
    }

    @Test
    void testMejaEventUpdatedNomor() {
        UUID mejaId = UUID.randomUUID();
        Integer oldNomor = 101;
        Integer newNomor = 102;
        MejaEvent event = createMejaEvent(MejaEvent.Type.UPDATED_NOMOR, mejaId, newNomor, oldNomor, "TERPAKAI");

        tableEventListener.handleTableEvent(event);

        verify(orderTableManagementService).handleTableNumberUpdated(eq(mejaId), eq(String.valueOf(oldNomor)), eq(String.valueOf(newNomor)));
        verify(orderTableManagementService, never()).handleTableDeleted(any(), any());
        verify(orderTableManagementService, never()).handleTableBecameAvailable(any(), any());
    }

    @Test
    void testMejaEventUpdatedStatusToTersedia() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 101;
        MejaEvent event = createMejaEvent(MejaEvent.Type.UPDATED_STATUS, mejaId, nomorMeja, null, "TERSEDIA");

        tableEventListener.handleTableEvent(event);

        verify(orderTableManagementService).handleTableBecameAvailable(eq(mejaId), eq(String.valueOf(nomorMeja)));
        verify(orderTableManagementService, never()).handleTableDeleted(any(), any());
        verify(orderTableManagementService, never()).handleTableNumberUpdated(any(), any(), any());
    }

    @Test
    void testMejaEventUpdatedStatusToTerpakai() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 101;
        MejaEvent event = createMejaEvent(MejaEvent.Type.UPDATED_STATUS, mejaId, nomorMeja, null, "TERPAKAI");

        tableEventListener.handleTableEvent(event);

        verify(orderTableManagementService, never()).handleTableBecameAvailable(any(), any());
    }

    @Test
    void testMejaEventCreated() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 101;
        MejaEvent event = createMejaEvent(MejaEvent.Type.CREATED, mejaId, nomorMeja, null, "TERSEDIA");

        tableEventListener.handleTableEvent(event);

        verify(orderTableManagementService, never()).handleTableDeleted(any(), any());
        verify(orderTableManagementService, never()).handleTableNumberUpdated(any(), any(), any());
        verify(orderTableManagementService, never()).handleTableBecameAvailable(any(), any());
    }

    @Test
    void testExceptionInService() {
        UUID mejaId = UUID.randomUUID();
        Integer nomorMeja = 101;
        MejaEvent event = createMejaEvent(MejaEvent.Type.DELETED, mejaId, nomorMeja, null, "TERPAKAI");

        doThrow(new RuntimeException("Service error")).when(orderTableManagementService).handleTableDeleted(any(), any());

        assertDoesNotThrow(() -> tableEventListener.handleTableEvent(event));
        verify(orderTableManagementService).handleTableDeleted(eq(mejaId), eq(nomorMeja));
    }
}