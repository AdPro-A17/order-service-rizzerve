package id.ac.ui.cs.advprog.orderservice.observer;

import id.ac.ui.cs.advprog.orderservice.dto.OrderDetailsEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.order-events}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key.order.event.created}")
    private String rkOrderCreated;

    @Value("${app.rabbitmq.routing-key.order.event.updated}")
    private String rkOrderUpdated;

    @Value("${app.rabbitmq.routing-key.order.event.completed}")
    private String rkOrderCompleted;

    public void publishOrderEvent(OrderDetailsEvent event) {
        String routingKey;
        switch (event.getEventType()) {
            case CREATED: routingKey = rkOrderCreated; break;
            case UPDATED: routingKey = rkOrderUpdated; break;
            case COMPLETED: routingKey = rkOrderCompleted; break;
            default:
                LOGGER.warn("Unknown order event type for routing: {}", event.getEventType());
                return;
        }
        LOGGER.info("Publishing OrderEvent: Type={}, OrderID={}, Table={}, RoutingKey={}",
                event.getEventType(), event.getOrderId(), event.getTableNumber(), routingKey);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }
}