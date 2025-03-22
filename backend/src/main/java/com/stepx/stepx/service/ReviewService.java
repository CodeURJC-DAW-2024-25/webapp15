package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.stepx.stepx.dto.ReviewDTO;
import com.stepx.stepx.model.Review;
import com.stepx.stepx.mapper.*;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;


    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsByShoe(Long shoeId) {
        return reviewRepository.findReviewsByShoeId(shoeId);
    }

    public Optional<ReviewDTO> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(review -> Optional.ofNullable(reviewMapper.toDTO(review)))
                .orElse(Optional.empty());
    }

    public void save(ReviewDTO reviewDto) {
        Review review = reviewMapper.toDomain(reviewDto);
        reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isPresent()) {
            reviewRepository.deleteById(id);

        } else {
            throw new IllegalArgumentException("Review not found");
        }
    }

    public List<Review> getPagedReviewsByShoeId(Long shoeId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return reviewRepository.findByShoeId(shoeId, pageable).getContent();
    }

    public List<ReviewDTO> convertToDTOReviewList(List<Review> reviews) {
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        
        for (Review review : reviews) {
            ReviewDTO reviewDTO = reviewMapper.toDTO(review);
            //ReviewDTO reviewDTO = new ReviewDTO(review.getId(), review.getDate(), review.getRating(), review.getDescription(), review.getShoe(), review.getUser());
            reviewDTOs.add(reviewDTO);
        }
        
        return reviewDTOs;
    }

    public List<Review> convertToReviewList(List<ReviewDTO> reviewDTOs) {
        List<Review> reviews = new ArrayList<>();

        for (ReviewDTO reviewDTO : reviewDTOs) {
            Review review = new Review();
            review.setId(reviewDTO.id());
            review.setDate(reviewDTO.date());
            review.setRating(reviewDTO.rating());
            review.setDescription(reviewDTO.description());

           
            Shoe shoe = new Shoe();
            shoe.setId(reviewDTO.shoeId());
            review.setShoe(shoe);


            User user = new User();
            user.setId(reviewDTO.userId());
            review.setUser(user);

            reviews.add(review);
        }

        return reviews;
    }

}
