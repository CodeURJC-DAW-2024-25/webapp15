package com.stepx.stepx.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.stepx.stepx.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    Review toDomain(ReviewDTO reviewDTO);
}
