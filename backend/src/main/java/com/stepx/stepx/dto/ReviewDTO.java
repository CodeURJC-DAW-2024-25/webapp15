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
) {

    public ReviewDTO(Long id, LocalDate date, int rating, String description, Shoe shoe, User user) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        this.id = id;
        this.date = date;
        this.rating = rating;
        this.description = description;
        this.shoe = shoe;
        this.user = user;
    }
}
