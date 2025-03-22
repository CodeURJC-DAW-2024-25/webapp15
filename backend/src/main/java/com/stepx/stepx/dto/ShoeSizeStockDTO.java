package com.stepx.stepx.dto;


public record ShoeSizeStockDTO(
    Long id,
    Long shoeId,
    String size,
    Integer stock
) {
}
