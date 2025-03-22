package com.stepx.stepx.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
    Long id,
    Long orderId,
    Long shoeId,
    String shoeName,
    Integer quantity,
    String size,
    BigDecimal price
) {}
