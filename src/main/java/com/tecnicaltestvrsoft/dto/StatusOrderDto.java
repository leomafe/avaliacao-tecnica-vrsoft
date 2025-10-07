package com.tecnicaltestvrsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusOrderDto {

    private UUID idOrder;
    private Status status;
    private LocalDateTime dateProcessing;
    private String mensageError;
}
