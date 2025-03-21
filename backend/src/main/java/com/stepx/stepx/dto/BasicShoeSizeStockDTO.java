package com.stepx.stepx.dto;


public record BasicShoeSizeStockDTO(
    Long id,
    Long shoeId,
    String size,
    Integer stock
) {

}
