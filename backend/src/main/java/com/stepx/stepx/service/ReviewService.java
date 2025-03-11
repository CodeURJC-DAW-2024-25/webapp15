package com.stepx.stepx.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

import com.stepx.stepx.model.Review;

import com.stepx.stepx.repository.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsByShoe(Long shoeId) {
        return reviewRepository.findReviewsByShoeId(shoeId);
    }

    public void save(Review review) {
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

}
