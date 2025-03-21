package com.stepx.stepx.dto;

import java.math.BigDecimal;

public record CouponDTO(
    Long id,
    String code,
    BigDecimal discount
) {
}