package com.tecnicaltestvrsoft.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatusService {

    private final Map<UUID, String> statusOrdens = new ConcurrentHashMap<>();

    public void updateStatus(UUID pedidoId, String status) {
        statusOrdens.put(pedidoId, status);
        System.out.println("Status updated: " + pedidoId + " -> " + status);
    }

    public Optional<String> getStatus(UUID pedidoId) {
        return Optional.ofNullable(statusOrdens.get(pedidoId));
    }
}
