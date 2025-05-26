package id.ac.ui.cs.advprog.orderservice.observer;

import id.ac.ui.cs.advprog.orderservice.dto.MejaEvent;
import id.ac.ui.cs.advprog.orderservice.service.OrderTableManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TableEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableEventListener.class);

    private final OrderTableManagementService orderTableManagementService;

    @Autowired
    public TableEventListener(OrderTableManagementService orderTableManagementService) {
        this.orderTableManagementService = orderTableManagementService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.order-service.for-table-events}")
    public void handleTableEvent(@Payload MejaEvent mejaEvent) {
        LOGGER.info("Received MejaEvent: Type={}, MejaID={}, NomorMeja={}, OldNomor={}, Status={}",
                mejaEvent.getType(), mejaEvent.getMejaId(), mejaEvent.getNomorMeja(),
                mejaEvent.getOldNomor(), mejaEvent.getStatus());

        try {
            switch (mejaEvent.getType()) {
                case DELETED:
                    LOGGER.info("Processing DELETED event for Meja ID: {}", mejaEvent.getMejaId());
                    orderTableManagementService.handleTableDeleted(mejaEvent.getMejaId(), mejaEvent.getNomorMeja());
                    break;
                case UPDATED_NOMOR:
                    LOGGER.info("Processing UPDATED_NOMOR event for Meja ID: {}. Old: {}, New: {}",
                            mejaEvent.getMejaId(), mejaEvent.getOldNomor(), mejaEvent.getNomorMeja());
                    orderTableManagementService.handleTableNumberUpdated(
                            mejaEvent.getMejaId(),
                            String.valueOf(mejaEvent.getOldNomor()),
                            String.valueOf(mejaEvent.getNomorMeja())
                    );
                    break;
                case UPDATED_STATUS:
                    LOGGER.info("Processing UPDATED_STATUS event for Meja ID: {}. Status: {}",
                            mejaEvent.getMejaId(), mejaEvent.getStatus());
                    if ("TERSEDIA".equalsIgnoreCase(mejaEvent.getStatus())) {
                        orderTableManagementService.handleTableBecameAvailable(mejaEvent.getMejaId(), String.valueOf(mejaEvent.getNomorMeja()));
                    }
                    break;
                case CREATED:
                    LOGGER.info("Processing CREATED event for Meja ID: {}. Nomor: {}", mejaEvent.getMejaId(), mejaEvent.getNomorMeja());
                    break;
                default:
                    LOGGER.warn("Received unhandled MejaEvent type: {}", mejaEvent.getType());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing MejaEvent: " + mejaEvent.toString(), e);
        }
    }
}