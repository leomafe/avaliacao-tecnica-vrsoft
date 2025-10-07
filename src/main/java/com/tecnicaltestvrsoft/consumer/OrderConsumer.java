package com.tecnicaltestvrsoft.consumer;

import com.tecnicaltestvrsoft.dto.OrderDto;
import com.tecnicaltestvrsoft.dto.Status;
import com.tecnicaltestvrsoft.dto.StatusOrderDto;
import com.tecnicaltestvrsoft.exeception.ProcessingException;
import com.tecnicaltestvrsoft.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    private final Random random = new Random();

    private final RabbitTemplate rabbitTemplate;

    private final StatusService statusService;

    @Value("${rabbitmq.exchange.status.name}")
    private String statusExchangeName;
    @Value("${rabbitmq.routingkey.status.sucess}")
    private String sucessRoutingKey;
    @Value("${rabbitmq.routingkey.status.fail}")
    private String failRoutingKey;

    public OrderConsumer(RabbitTemplate rabbitTemplate, StatusService statusService) {
        this.rabbitTemplate = rabbitTemplate;
        this.statusService = statusService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processOrder(OrderDto order) throws InterruptedException {

        statusService.updateStatus(order.getId(), "PROCESSING");

        LOGGER.info("[START] Processing ID order: {}", order.getId());
        try {
            long processingTime = 1000 + random.nextInt(2000);
            TimeUnit.MILLISECONDS.sleep(processingTime);
            if (random.nextDouble() < 0.2) {
                throw new ProcessingException("Simulated order processing failure " + order.getId());
            }
            StatusOrderDto statusSucess = new StatusOrderDto(order.getId(), Status.SUCESS, LocalDateTime.now(), null);
            rabbitTemplate.convertAndSend(statusExchangeName, sucessRoutingKey, statusSucess);
        } catch (ProcessingException e) {
            LOGGER.error("[FAIL] Error processing order ID: {}. Reason: {}", order.getId(), e.getMessage());
            StatusOrderDto statusFail = new StatusOrderDto(order.getId(), Status.FAIL, LocalDateTime.now(), e.getMessage());
            rabbitTemplate.convertAndSend(statusExchangeName, failRoutingKey, statusFail);

            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
