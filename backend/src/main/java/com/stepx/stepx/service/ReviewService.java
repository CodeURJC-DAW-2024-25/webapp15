package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.stepx.stepx.dto.ReviewDTO;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.Review;
import com.stepx.stepx.mapper.*;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewDTO> getReviewsByShoe(Long shoeId) {
        System.out.println("Fetching reviews for shoe with ID: " + shoeId);
        List<Review> reviews = reviewRepository.findReviewsByShoeId(shoeId);
        System.out.println("Reviews retrieved from DB: " + reviews);
        return reviews != null ? reviewMapper.toDTOs(reviews) : List.of();
    }

    public ReviewDTO getReviewById(Long id) {
        Optional<Review> reviewDto = reviewRepository.findById(id);
        if (reviewDto.isPresent()) {
            return reviewMapper.toDTO(reviewDto.get());
        } else {
            return null;
        }
    }

    public ReviewDTO save(ReviewDTO reviewDto) {
        Shoe shoe = shoeRepository.findById(reviewDto.shoeId()).orElseThrow();
        User user = userRepository.findById(reviewDto.userId()).orElseThrow();

        if (user==null){
            throw new NoSuchElementException();
        }

        Review review = new Review();
        review.setRating(reviewDto.rating());
        review.setDescription(reviewDto.description());
        review.setShoe(shoe);
        review.setUser(user);
        review.setDate(reviewDto.date());
        shoe.addReview(review);

        System.out.println("⚙️ Review in shoe: " + shoe.getReviews());
        Review Savedreview = reviewRepository.save(review);
        shoeRepository.save(shoe);
        return reviewMapper.toDTO(Savedreview);
    }

    public Optional<ReviewDTO> deleteReview(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isPresent()) {
            reviewRepository.deleteById(id);
            return Optional.of(reviewMapper.toDTO(reviewOptional.get()));

        }
        return Optional.empty();
    }

    public List<ReviewDTO> getPagedReviewsByShoeId(Long shoeId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<Review> reviews = reviewRepository.findByShoeId(shoeId, pageable).getContent();
        return reviews != null ? reviewMapper.toDTOs(reviews) : List.of();
    }

    public List<ReviewDTO> convertToDTOReviewList(List<Review> reviews) {
        List<ReviewDTO> reviewDTOs = new ArrayList<>();

        for (Review review : reviews) {
            ReviewDTO reviewDTO = reviewMapper.toDTO(review);
            // ReviewDTO reviewDTO = new ReviewDTO(review.getId(), review.getDate(),
            // review.getRating(), review.getDescription(), review.getShoe(),
            // review.getUser());
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

    public ReviewDTO assignIdshoe(ReviewDTO reviewDto, Long idShoe) {
        Review review = reviewMapper.toDomain(reviewDto);
        Shoe shoe = shoeRepository.findById(idShoe).orElse(null);
        review.setShoe(shoe);
        reviewDto = reviewMapper.toDTO(review);
        return reviewDto;
    }

    public Optional<ReviewDTO> updateReview(Long id, ReviewDTO reviewDto) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setDate(reviewDto.date());
            review.setRating(reviewDto.rating());
            review.setDescription(reviewDto.description());
            reviewRepository.save(review);
            return Optional.of(reviewMapper.toDTO(review));
        } else {
            return Optional.empty();
        }
    }

}
