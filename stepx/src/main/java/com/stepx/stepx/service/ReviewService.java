package com.stepx.stepx.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.Review;

import com.stepx.stepx.repository.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    //public List<Review> getReviews(long id) {
      //  return reviewRepository.findById(id);
    //}

    public List<Review> getReviewsByShoe(Long shoeId) {
        return reviewRepository.findReviewsByShoeId(shoeId);
    }
    

}
