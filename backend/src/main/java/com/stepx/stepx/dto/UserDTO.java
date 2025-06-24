package com.stepx.stepx.dto;

import java.util.List;

public record UserDTO(
    Long id,
    String imageString,
    String firstname,
    String lastName,
    List<String> roles,
    String username,
    String email,
    List<OrderShoesDTO> orders,
    String password
) {
}