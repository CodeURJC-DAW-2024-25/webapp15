package com.stepx.stepx.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stepx.stepx.service.*;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.UserDTO;

@RestController
@RequestMapping("/api/v1/index")
public class IndexRestController {

   

    @Autowired
    private OrderItemService orderItemService;

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
        Optional<UserDTO> user = userService.findUserByUserName(username); // Suponiendo que tienes un método para
                                                                           // encontrar al usuario por su nombre de
                                                                           // usuario

        if (user.isEmpty()) {
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

    @GetMapping("/recommended-products/users/{userId}")
    public ResponseEntity<Map<String, Object>> getRecommendedProducts(
            @PathVariable Long userId, // Recibimos el userId como parámetro
            @RequestParam(defaultValue = "10") int limit) { // El límite de productos recomendados, por defecto 10

        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            response.put("recommendedProducts", "User not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        String username = authentication.getName();
        Optional<UserDTO> authenticatedUser = userService.findUserByUserName(username);

        if (authenticatedUser.isEmpty()) {
            response.put("recommendedProducts", "Authenticated user not found");
            return ResponseEntity.status(404).body(response);
        }

        // ✅ Corregimos: obtenemos bien los roles
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));

        // ✅ Comparar correctamente el ID
        if (!isAdmin && !authenticatedUser.get().id().equals(userId)) {
            response.put("recommendedProducts", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        // 2 Verificamos si el usuario existe en la base de datos
        UserDTO user = userService.findUserById(userId); // Método para encontrar el usuario por su ID

        if (user == null) {
            response.put("recommendedProducts", "User not found");
            return ResponseEntity.status(404).body(response); // Si el usuario no existe, respondemos con error 404
        }

        // Obtener productos recomendados para el usuario específico
        List<ShoeDTO> recommendedShoes = shoeService.getRecommendedShoesForUser(userId, limit); // Método en shoeService

        if (recommendedShoes.isEmpty()) {
            response.put("recommendedProducts", "No products found");
        } else {
            response.put("recommendedProducts", recommendedShoes);
        }

        return ResponseEntity.ok(response); // Respondemos con los productos recomendados
    }

    @GetMapping("/bestSelling")
    public ResponseEntity<Map<String, Object>> getBestSellingProducts(
            @RequestParam(defaultValue = "10") int limit) { // Límite de productos, por defecto 10

        Map<String, Object> response = new HashMap<>();

        // Obtener los productos más vendidos
        List<ShoeDTO> bestSellingShoes = orderItemService.getBestSellingShoes(limit);

        if (bestSellingShoes.isEmpty()) {
            response.put("bestSellingProducts", "No products found");
        } else {
            response.put("bestSellingProducts", bestSellingShoes);
        }

        return ResponseEntity.ok(response);
    }

}
