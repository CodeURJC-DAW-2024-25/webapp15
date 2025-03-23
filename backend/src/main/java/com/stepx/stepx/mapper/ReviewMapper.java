package com.stepx.stepx.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.stepx.stepx.dto.ReviewDTO;
import com.stepx.stepx.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper{

    @Mapping(source = "shoe.id", target = "shoeId")
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    Review toDomain(ReviewDTO reviewDTO);

    
}
