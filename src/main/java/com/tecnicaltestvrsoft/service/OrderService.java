package com.tecnicaltestvrsoft.service;

import com.tecnicaltestvrsoft.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    public void sendOrderToQueue(OrderDto order) {
        LOGGER.info("Posting ordem to queue {}: {}", queueName, order.getId());

        rabbitTemplate.convertAndSend(queueName, order);

        LOGGER.info("Order {} published successfully!", order.getId());
    }

}
