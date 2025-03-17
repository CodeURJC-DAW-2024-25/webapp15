package com.stepx.stepx.dto;

public record OrderItemDTO(
    Long id,
    Long orderId,
    Long shoeId,
    String shoeName,
    Integer quantity,
    String size
) {

 }
