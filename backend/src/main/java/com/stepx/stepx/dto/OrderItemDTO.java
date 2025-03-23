package com.stepx.stepx.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderItemDTO(
    Long id,

    @NotNull(message = "Id of order is required")
    Long orderId,

    @NotNull(message = "Id of shoe is required")
    Long shoeId,

    String shoeName,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "quantity must be greater than 0")
    Integer quantity,

    @NotBlank(message = "Size is required")
    String size,

    BigDecimal price
) {}
