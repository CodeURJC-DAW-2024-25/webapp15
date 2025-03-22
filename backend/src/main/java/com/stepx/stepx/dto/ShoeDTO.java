package com.stepx.stepx.dto;

import java.math.BigDecimal;
import java.util.List;

public record ShoeDTO(
    Long id,
    String name,
    String shortDescription,
    String longDescription,
    BigDecimal price,
    String brand,
    String category,
    String imageUrl1,
    String imageUrl2,
    String imageUrl3,
    List<ShoeSizeStockDTO> sizeStocks,
    List<ReviewDTO> reviews
) {}