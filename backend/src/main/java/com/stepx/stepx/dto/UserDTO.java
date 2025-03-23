package com.stepx.stepx.dto;

import java.sql.Blob;
import java.util.List;

public record UserDTO(
    Long id,
    String imageUser,
    String firstname,
    String lastName,
    List<String> roles,
    String username,
    String email,
    List<OrderShoesDTO> orders,
    String password
) {
}