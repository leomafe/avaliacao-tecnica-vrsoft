package com.tecnicaltestvrsoft.controller;

import com.tecnicaltestvrsoft.dto.OrderDto;
import com.tecnicaltestvrsoft.service.OrderService;
import com.tecnicaltestvrsoft.service.StatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class OrderController {

    private final OrderService orderService;
    private final StatusService statusService;

    public OrderController(OrderService orderService, StatusService statusService) {

        this.orderService = orderService;
        this.statusService = statusService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@Valid @RequestBody OrderDto order) {
        order.setId(UUID.randomUUID());
        order.setCreated(LocalDateTime.now());

        statusService.updateStatus(order.getId(), "RECEIVED, WAITING FOR PROCESSING");
        orderService.sendOrderToQueue(order);

        Map<String, String> response = Map.of(
                "message", "Order received and will be processed.",
                "pedidoId", order.getId().toString()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, String>> getStatusOrder(@PathVariable UUID id) {
        return statusService.getStatus(id)
                .map(status -> {
                    Map<String, String> response = Map.of("orderId", id.toString(), "status", status);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, String> response = Map.of("error", "Order not found", "orderId", id.toString());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }




}
