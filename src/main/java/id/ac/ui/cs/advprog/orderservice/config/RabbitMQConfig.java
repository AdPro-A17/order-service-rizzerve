package id.ac.ui.cs.advprog.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Value("${app.rabbitmq.exchange.table-events}")
    private String tableEventsExchangeName;

    @Value("${app.rabbitmq.queue.order-service.for-table-events}")
    private String orderServiceTableEventsQueueName;

    @Value("${app.rabbitmq.routing-key.table.event.deleted}")
    private String routingKeyTableDeleted;

    @Value("${app.rabbitmq.routing-key.table.event.updated.nomor}")
    private String routingKeyTableUpdatedNomor;

    @Bean
    TopicExchange tableEventsExchange() {
        return new TopicExchange(tableEventsExchangeName, true, false);
    }

    @Bean
    Queue orderServiceTableEventsQueue() {
        return new Queue(orderServiceTableEventsQueueName, true);
    }

    @Bean
    Binding tableDeletedBinding(Queue orderServiceTableEventsQueue, TopicExchange tableEventsExchange) {
        return BindingBuilder.bind(orderServiceTableEventsQueue)
                .to(tableEventsExchange)
                .with(routingKeyTableDeleted);
    }

    @Bean
    Binding tableUpdatedNomorBinding(Queue orderServiceTableEventsQueue, TopicExchange tableEventsExchange) {
        return BindingBuilder.bind(orderServiceTableEventsQueue)
                .to(tableEventsExchange)
                .with(routingKeyTableUpdatedNomor);
    }

    @Value("${app.rabbitmq.exchange.order-events}")
    private String orderEventsExchangeNamePublisher;

    @Bean
    TopicExchange orderEventsExchangePublisher() {
        return new TopicExchange(orderEventsExchangeNamePublisher, true, false);
    }
}