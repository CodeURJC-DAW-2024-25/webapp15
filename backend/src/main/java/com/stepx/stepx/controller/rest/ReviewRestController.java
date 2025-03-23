package com.stepx.stepx.controller.rest;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
import com.stepx.stepx.model.Review;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.service.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ShoeRepository shoeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShoeService shoeService;
    @Autowired
    private UserService userService;

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
            return ResponseEntity.noContent().build(); // 204 No Content si no hay reseñas
        }
        return ResponseEntity.ok(reviews);
    }

    // Crear una review
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
                .buildAndExpand(savedDTO.id()) // Asegúrate de tener un método getId() en ReviewDTO
                .toUri();

        System.out.println("✅ Creada Review: " + savedDTO);

        return ResponseEntity.created(location).body(savedDTO);
    }

    // Eliminar una review
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable Long id) {
        Optional<ReviewDTO> deleted = reviewService.deleteReview(id); // Implementa este método en el servicio

        return deleted.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // update a orderitem
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDto) {
        Optional<ReviewDTO> review = reviewService.updateReview(id, reviewDto);// get the orderitem
        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review.get());
    }

}
