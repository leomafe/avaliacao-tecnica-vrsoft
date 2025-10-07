package com.tecnicaltestvrsoft.service;

import com.tecnicaltestvrsoft.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "queueName", "ordens.entry");
    }

    @Test
    @DisplayName("Must publish a request message to the queue successfully")
    void mustPublishMensageQueuesuccessfully() {
        OrderDto pedido = new OrderDto();
        pedido.setId(UUID.randomUUID());
        pedido.setProduct("Notebook");
        pedido.setQuantity(1);

        orderService.sendOrderToQueue(pedido);

        ArgumentCaptor<String> queueNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderDto> pedidoCaptor = ArgumentCaptor.forClass(OrderDto.class);

        verify(rabbitTemplate).convertAndSend(queueNameCaptor.capture(), pedidoCaptor.capture());

        // 3. Verificamos se os argumentos capturados são os que esperávamos.
        assertEquals("ordens.entry", queueNameCaptor.getValue());
        assertEquals(pedido.getId(), pedidoCaptor.getValue().getId());
        assertEquals("Notebook", pedidoCaptor.getValue().getProduct());
    }
}
