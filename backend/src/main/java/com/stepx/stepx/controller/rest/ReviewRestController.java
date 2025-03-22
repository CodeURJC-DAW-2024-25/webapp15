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

@PostMapping("/submit/{idShoe}")
public ResponseEntity<?> publishReview(
        @PathVariable Long idShoe,
        @RequestParam("rating") int rating,
        @RequestParam String description,
        HttpServletRequest request
) {
    // Verificar autenticación del usuario
    if (request.getUserPrincipal() == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Usuario no autenticado.");
    }

    // Obtener el usuario autenticado
    String username = request.getUserPrincipal().getName();
    UserDTO userDto = userService.findUserByUserName(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Buscar el zapato por ID
    ShoeDTO shoeDto = shoeService.getShoeById(idShoe)
            .orElseThrow(() -> new RuntimeException("Shoe not found"));

    // Crear la reseña
    ReviewDTO reviewDto = new ReviewDTO(null, LocalDate.now(), rating, description, idShoe, userDto.id());

    // Guardar la reseña y obtener el ID generado
    reviewService.save(reviewDto);

    // Construir la URI del recurso recién creado
    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(reviewDto.id()) // Suponiendo que `getId()` retorna el ID de la reseña
            .toUri();

    // Retornar la respuesta con código 201 Created y la URI en `Location`
    return ResponseEntity.created(location).body(reviewDto);
}

    

    @DeleteMapping("/reviews/{id}")  // Cambio de nombre para seguir convenciones REST
public ResponseEntity<Object> deleteReview(@PathVariable Long id, HttpServletRequest request) {
    // Verificar si el usuario es dueño de la reseña o un administrador
    if (!request.isUserInRole("ROLE_ADMIN")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar esta reseña.");
    }

    try {
        reviewService.deleteReview(id);
			return new ResponseEntity<>(null, HttpStatus.OK);

		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
}

    
}
