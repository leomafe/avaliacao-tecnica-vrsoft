package com.tecnicaltestvrsoft.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDto {

    private UUID id;

    @NotBlank(message = "Name of product cannot be empty")
    private String product;

    @Min(value = 1, message = "The quantity must be greater than zero")
    private int quantity;

    private LocalDateTime created;
}
