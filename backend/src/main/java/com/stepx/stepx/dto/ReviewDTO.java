package com.stepx.stepx.dto;

import java.time.LocalDate;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;


public record ReviewDTO(
    Long id,
    LocalDate date,
    int rating,
    String description,
    Shoe shoe,
    User user
) {}
