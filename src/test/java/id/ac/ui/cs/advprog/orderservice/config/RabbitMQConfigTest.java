package id.ac.ui.cs.advprog.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    private RabbitMQConfig rabbitMQConfig;

    @BeforeEach
    void setUp() {
        rabbitMQConfig = new RabbitMQConfig();
        
        // Set the @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(rabbitMQConfig, "tableEventsExchangeName", "table.events.exchange");
        ReflectionTestUtils.setField(rabbitMQConfig, "orderServiceTableEventsQueueName", "order-service.table-events.queue");
        ReflectionTestUtils.setField(rabbitMQConfig, "routingKeyTableDeleted", "table.event.deleted");
        ReflectionTestUtils.setField(rabbitMQConfig, "routingKeyTableUpdatedNomor", "table.event.updated.nomor");
        ReflectionTestUtils.setField(rabbitMQConfig, "orderEventsExchangeNamePublisher", "order.events.exchange");
    }

    @Test
    void testJsonMessageConverter() {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        MessageConverter converter = rabbitMQConfig.jsonMessageConverter(objectMapper);

        // Assert
        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);
    }

    @Test
    void testTableEventsExchange() {
        // Act
        TopicExchange exchange = rabbitMQConfig.tableEventsExchange();

        // Assert
        assertNotNull(exchange);
        assertEquals("table.events.exchange", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    void testOrderServiceTableEventsQueue() {
        // Act
        Queue queue = rabbitMQConfig.orderServiceTableEventsQueue();

        // Assert
        assertNotNull(queue);
        assertEquals("order-service.table-events.queue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void testTableDeletedBinding() {
        // Arrange
        Queue queue = rabbitMQConfig.orderServiceTableEventsQueue();
        TopicExchange exchange = rabbitMQConfig.tableEventsExchange();

        // Act
        Binding binding = rabbitMQConfig.tableDeletedBinding(queue, exchange);

        // Assert
        assertNotNull(binding);
        assertEquals("table.event.deleted", binding.getRoutingKey());
        assertEquals(queue.getName(), binding.getDestination());
        assertEquals(exchange.getName(), binding.getExchange());
        assertEquals(Binding.DestinationType.QUEUE, binding.getDestinationType());
    }

    @Test
    void testTableUpdatedNomorBinding() {
        // Arrange
        Queue queue = rabbitMQConfig.orderServiceTableEventsQueue();
        TopicExchange exchange = rabbitMQConfig.tableEventsExchange();

        // Act
        Binding binding = rabbitMQConfig.tableUpdatedNomorBinding(queue, exchange);

        // Assert
        assertNotNull(binding);
        assertEquals("table.event.updated.nomor", binding.getRoutingKey());
        assertEquals(queue.getName(), binding.getDestination());
        assertEquals(exchange.getName(), binding.getExchange());
        assertEquals(Binding.DestinationType.QUEUE, binding.getDestinationType());
    }

    @Test
    void testOrderEventsExchangePublisher() {
        // Act
        TopicExchange exchange = rabbitMQConfig.orderEventsExchangePublisher();

        // Assert
        assertNotNull(exchange);
        assertEquals("order.events.exchange", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    void testAllBeansAreCreated() {
        // Test that all beans can be created without exceptions
        assertDoesNotThrow(() -> {
            rabbitMQConfig.tableEventsExchange();
            rabbitMQConfig.orderServiceTableEventsQueue();
            rabbitMQConfig.orderEventsExchangePublisher();
        });
    }

    @Test
    void testBindingsWithDifferentExchanges() {
        // Arrange
        Queue queue = rabbitMQConfig.orderServiceTableEventsQueue();
        TopicExchange exchange1 = rabbitMQConfig.tableEventsExchange();
        TopicExchange exchange2 = rabbitMQConfig.orderEventsExchangePublisher();

        // Act
        Binding binding1 = rabbitMQConfig.tableDeletedBinding(queue, exchange1);
        Binding binding2 = rabbitMQConfig.tableUpdatedNomorBinding(queue, exchange1);

        // Assert
        assertNotNull(binding1);
        assertNotNull(binding2);
        assertNotEquals(binding1.getRoutingKey(), binding2.getRoutingKey());
        assertEquals(binding1.getExchange(), binding2.getExchange());
        assertEquals(binding1.getDestination(), binding2.getDestination());
    }

    @Test
    void testExchangeProperties() {
        // Act
        TopicExchange tableExchange = rabbitMQConfig.tableEventsExchange();
        TopicExchange orderExchange = rabbitMQConfig.orderEventsExchangePublisher();

        // Assert
        // Both exchanges should be durable and not auto-delete
        assertTrue(tableExchange.isDurable());
        assertFalse(tableExchange.isAutoDelete());
        assertTrue(orderExchange.isDurable());
        assertFalse(orderExchange.isAutoDelete());
        
        // They should have different names
        assertNotEquals(tableExchange.getName(), orderExchange.getName());
    }

    @Test
    void testQueueProperties() {
        // Act
        Queue queue = rabbitMQConfig.orderServiceTableEventsQueue();

        // Assert
        assertTrue(queue.isDurable());
        assertFalse(queue.isExclusive());
        assertFalse(queue.isAutoDelete());
    }
}