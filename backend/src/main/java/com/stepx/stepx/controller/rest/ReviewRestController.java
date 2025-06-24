package com.stepx.stepx.controller.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.service.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // get a order item by id
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long id) {
        ReviewDTO reviewDto = reviewService.getReviewById(id);

        if (reviewDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviewDto);
    }

    // get all reviews in shoe
    @GetMapping("/Shoe/{idShoe}")
    public ResponseEntity<List<ReviewDTO>> getAll(@PathVariable Long idShoe) {
        List<ReviewDTO> reviews = reviewService.getReviewsByShoe(idShoe);
        System.out.println("foundedreviews:" + reviews);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(reviews);
    }

    
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        System.out.println("⚙️ Recibido DTO: " + reviewDTO);

        ReviewDTO savedDTO = reviewService.save(reviewDTO);

        if (savedDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDTO.id())
                .toUri();

        System.out.println("✅ Creada Review: " + savedDTO);

        return ResponseEntity.created(location).body(savedDTO);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable Long id) {
        // 🔐 get autentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        // 🔐 Verificar si es admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
        }

        Optional<ReviewDTO> deleted = reviewService.deleteReview(id); 

        return deleted.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // update a orderitem
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDto) {

        // 🔐 get autentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        // 🔐 Verify if admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Access denied for non-admins
        }
        
        Optional<ReviewDTO> review = reviewService.updateReview(id, reviewDto);
        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review.get());
    }

    // Reviews
    @GetMapping("/pages/{id}")
    public ResponseEntity<List<ReviewDTO>> getPaginatedReviews(@PathVariable Long id, @RequestParam int page,
            @RequestParam int size) {
        List<ReviewDTO> reviews = reviewService.getPagedReviewsByShoeId(id, size, page);
        if (reviews == null) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.ok(reviews);
    }

}
