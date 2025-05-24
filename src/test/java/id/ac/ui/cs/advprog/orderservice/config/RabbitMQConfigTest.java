package id.ac.ui.cs.advprog.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQConfigTest {
    private RabbitMQConfig rabbitMQConfig;

    private final String tableEventsExchangeNameValue = "table.events.exchange.test";
    private final String orderServiceQueueNameValue = "order-service.queue.test";
    private final String rkTableDeletedValue = "table.deleted.test";
    private final String rkTableUpdatedNomorValue = "table.updated.nomor.test";
    private final String orderEventsExchangePublisherNameValue = "order.events.exchange.publisher.test";

    @BeforeEach
    void setUp() {
        rabbitMQConfig = new RabbitMQConfig();
        ReflectionTestUtils.setField(rabbitMQConfig, "tableEventsExchangeName", tableEventsExchangeNameValue);
        ReflectionTestUtils.setField(rabbitMQConfig, "orderServiceTableEventsQueueName", orderServiceQueueNameValue);
        ReflectionTestUtils.setField(rabbitMQConfig, "routingKeyTableDeleted", rkTableDeletedValue);
        ReflectionTestUtils.setField(rabbitMQConfig, "routingKeyTableUpdatedNomor", rkTableUpdatedNomorValue);
        ReflectionTestUtils.setField(rabbitMQConfig, "orderEventsExchangeNamePublisher", orderEventsExchangePublisherNameValue);
    }

    @Test
    void testJsonMessageConverterBean() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        MessageConverter messageConverter = rabbitMQConfig.jsonMessageConverter(objectMapper);
        assertNotNull(messageConverter);
        assertTrue(messageConverter instanceof Jackson2JsonMessageConverter);
    }

    @Test
    void testTableEventsExchangeBean() {
        TopicExchange exchange = rabbitMQConfig.tableEventsExchange();
        assertNotNull(exchange);
        assertEquals(tableEventsExchangeNameValue, exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    void testOrderServiceTableEventsQueueBean() {
        Queue queue = rabbitMQConfig.orderServiceTableEventsQueue();
        assertNotNull(queue);
        assertEquals(orderServiceQueueNameValue, queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void testTableDeletedBinding() {
        Queue queue = new Queue(orderServiceQueueNameValue);
        TopicExchange exchange = new TopicExchange(tableEventsExchangeNameValue);
        Binding binding = rabbitMQConfig.tableDeletedBinding(queue, exchange);
        assertNotNull(binding);
        assertEquals(orderServiceQueueNameValue, binding.getDestination());
        assertEquals(tableEventsExchangeNameValue, binding.getExchange());
        assertEquals(rkTableDeletedValue, binding.getRoutingKey());
    }

    @Test
    void testTableUpdatedNomorBinding() {
        Queue queue = new Queue(orderServiceQueueNameValue);
        TopicExchange exchange = new TopicExchange(tableEventsExchangeNameValue);
        Binding binding = rabbitMQConfig.tableUpdatedNomorBinding(queue, exchange);
        assertNotNull(binding);
        assertEquals(orderServiceQueueNameValue, binding.getDestination());
        assertEquals(tableEventsExchangeNameValue, binding.getExchange());
        assertEquals(rkTableUpdatedNomorValue, binding.getRoutingKey());
    }

    @Test
    void testOrderEventsExchangePublisherBean() {
        TopicExchange exchange = rabbitMQConfig.orderEventsExchangePublisher();
        assertNotNull(exchange);
        assertEquals(orderEventsExchangePublisherNameValue, exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }
}