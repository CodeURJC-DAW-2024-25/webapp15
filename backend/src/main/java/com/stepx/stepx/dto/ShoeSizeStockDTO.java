package com.stepx.stepx.dto;

public record ShoeSizeStockDTO(
    Long id,
    Long shoeId,
    String shoeName,
    String size,
    Integer stock
) { }
