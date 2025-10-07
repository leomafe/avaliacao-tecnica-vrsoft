package com.tecnicaltestvrsoft.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.queue.dlq.name}")
    private String dlqQueueName;

    @Value("${rabbitmq.queue.status.sucess.name}")
    private String statusSucessQueueName;

    @Value("${rabbitmq.queue.status.fail.name}")
    private String statusFailQueueName;

    @Value("${rabbitmq.exchange.deadletter.name}")
    private String deadLetterExchangeName;

    @Value("${rabbitmq.exchange.status.name}")
    private String statusExchangeName;

    @Value("${rabbitmq.routingkey.status.sucess}")
    private String sucessRoutingKey;

    @Value("${rabbitmq.routingkey.status.fail}")
    private String failRoutingKey;

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName);
    }

    @Bean
    public Queue dlq() {
        return new Queue(dlqQueueName, true);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with("");
    }

    @Bean
    public Queue queue() {

        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-dead-letter-routing-key", "")
                .build();
    }

    @Bean
    public TopicExchange statusExchange() {
        return new TopicExchange(statusExchangeName);
    }

    @Bean
    public Queue statusSucessQueue() {
        return new Queue(statusSucessQueueName, true);
    }

    @Bean
    public Queue statusFailQueue() {
        return new Queue(statusFailQueueName, true);
    }

    @Bean
    public Binding statusSucessBinding(Queue statusSucessQueue, TopicExchange statusExchange) {
        return BindingBuilder.bind(statusSucessQueue).to(statusExchange).with(sucessRoutingKey);
    }

    @Bean
    public Binding statusFalhaBinding(Queue statusFailQueue, TopicExchange statusExchange) {
        return BindingBuilder.bind(statusFailQueue).to(statusExchange).with(failRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
