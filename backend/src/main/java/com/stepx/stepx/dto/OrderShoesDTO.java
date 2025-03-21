package com.stepx.stepx.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderShoesDTO(
    Long id,
    LocalDate date,
    String cuponUsed,
    String country,
    String firstName,
    String secondName,
    String email,
    String address,
    String numerPhone,
    BigDecimal summary,
    String state,
    Long userId,
    CouponDTO coupon,
    List<OrderItemDTO> orderItems
) {}
