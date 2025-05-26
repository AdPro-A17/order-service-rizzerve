package id.ac.ui.cs.advprog.orderservice.observer;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderEventPublisher orderEventPublisher;

    private final String exchangeName = "order.events.exchange.test";
    private final String rkCreated = "order.created.test";
    private final String rkUpdated = "order.updated.test";
    private final String rkCompleted = "order.completed.test";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderEventPublisher, "exchangeName", exchangeName);
        ReflectionTestUtils.setField(orderEventPublisher, "rkOrderCreated", rkCreated);
        ReflectionTestUtils.setField(orderEventPublisher, "rkOrderUpdated", rkUpdated);
        ReflectionTestUtils.setField(orderEventPublisher, "rkOrderCompleted", rkCompleted);
    }

    private OrderDetailsEvent createSampleEvent(OrderDetailsEvent.EventType eventType) {
        return OrderDetailsEvent.builder()
                .eventType(eventType)
                .orderId(UUID.randomUUID())
                .tableNumber("101")
                .orderStatus("NEW")
                .totalPrice(100.0)
                .items(Collections.emptyList())
                .occurredAt(Instant.now())
                .build();
    }

    @Test
    void publishOrderCreatedEvent() {
        OrderDetailsEvent event = createSampleEvent(OrderDetailsEvent.EventType.CREATED);
        orderEventPublisher.publishOrderEvent(event);
        verify(rabbitTemplate).convertAndSend(exchangeName, rkCreated, event);
    }

    @Test
    void publishOrderUpdatedEvent() {
        OrderDetailsEvent event = createSampleEvent(OrderDetailsEvent.EventType.UPDATED);
        orderEventPublisher.publishOrderEvent(event);
        verify(rabbitTemplate).convertAndSend(exchangeName, rkUpdated, event);
    }

    @Test
    void publishOrderCompletedEvent() {
        OrderDetailsEvent event = createSampleEvent(OrderDetailsEvent.EventType.COMPLETED);
        orderEventPublisher.publishOrderEvent(event);
        verify(rabbitTemplate).convertAndSend(exchangeName, rkCompleted, event);
    }
}