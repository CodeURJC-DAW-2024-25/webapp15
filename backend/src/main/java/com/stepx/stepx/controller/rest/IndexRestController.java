package com.stepx.stepx.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.*;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.repository.OrderShoesRepository;

@RestController
@RequestMapping("/api/v1/index")
public class IndexRestController {

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private ShoeService shoeService;
    @Autowired
    private UserService userService;

    @GetMapping("/recommended-products")
public ResponseEntity<Map<String, Object>> getRecommendedProducts(
        @RequestParam(defaultValue = "10") int limit) {

    Map<String, Object> response = new HashMap<>();

    // Obtener el usuario autenticado del contexto de seguridad
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    // Verificar si el usuario está autenticado
    if (authentication == null || authentication.getPrincipal() instanceof String) {
        response.put("recommendedProducts", "User not authenticated");
        return ResponseEntity.status(401).body(response); // Si no está autenticado, respuesta de error
    }

    // Obtener el usuario actual a partir del principal (username)
    String username = authentication.getName();
    Optional<UserDTO> user = userService.findUserByUserName(username); // Suponiendo que tienes un método para encontrar al usuario por su nombre de usuario

    if (user.isEmpty()){
        response.put("recommendedProducts", "User not found");
        return ResponseEntity.status(404).body(response); // Si no se encuentra el usuario, respuesta de error
    }

    // Obtener productos recomendados para el usuario actual
    List<ShoeDTO> recommendedShoes = shoeService.getRecommendedShoesForUser(user.get().id(), limit);

    if (recommendedShoes.isEmpty()) {
        response.put("recommendedProducts", "No products found");
    } else {
        response.put("recommendedProducts", recommendedShoes);
    }

    return ResponseEntity.ok(response);
}

}
