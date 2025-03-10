package com.stepx.stepx.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

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

    public void save(Review review) {
        reviewRepository.save(review);
    }
    public void deleteReview(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isPresent()) {
            reviewRepository.deleteById(id);
            System.out.println("✅ Review con ID " + id + " eliminada correctamente.");
        } else {
            System.out.println("⚠️ No se encontró la review con ID " + id);
        }
    }

    public List<Review> getPagedReviewsByShoeId(Long shoeId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);  // Asegura que 'page' comience en 0
        return reviewRepository.findByShoeId(shoeId, pageable).getContent();
    }
    

    

    

}
