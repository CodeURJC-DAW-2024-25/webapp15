package com.stepx.stepx.dto;

import java.math.BigDecimal;

public record BasicShoeDTO (
    Long id,
    String name,
    String shortDescription,
    String longDescription,
    BigDecimal price,
    String brand,
    String category,
    String imageUrl1,
    String imageUrl2,
    String imageUrl3

){

}
