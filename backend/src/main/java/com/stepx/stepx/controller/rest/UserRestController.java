package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.*;
import com.stepx.stepx.service.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    // get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        if (users == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    // get a user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDto = userService.findUserById(id);

        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/chartuser/{userId}")
    public ResponseEntity<Map<String, Object>> getUserMonthlySpendingChart(@PathVariable Long userId) {
        
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
        
        
        
        // Get monthly spending data for the user
        List<Map<String, Object>> monthlySpending = userService.getMonthlySpendingByUserId(userId);

        // Prepare data for the chart
        Map<String, Object> chartData = new HashMap<>();
        String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        Double[] spendingData = new Double[12];

        // Initialize with zeros
        for (int i = 0; i < 12; i++) {
            spendingData[i] = 0.0;
        }

        // Fill in the actual spending data
        for (Map<String, Object> entry : monthlySpending) {
            String monthStr = (String) entry.get("month");
            Number amount = (Number) entry.get("total_spent");
            Double totalSpent = amount != null ? amount.doubleValue() : 0.0;

            // Convert month string to zero-based index
            int monthIndex = Integer.parseInt(monthStr) - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                spendingData[monthIndex] = totalSpent;
            }
        }

        chartData.put("labels", monthNames);
        chartData.put("data", spendingData);

        return ResponseEntity.ok(chartData);
    }

    // create User
    @PostMapping
    public ResponseEntity<UserDTO> createUserAPI(@RequestBody UserDTO userDto) {
        UserDTO createdUser = userService.createUserAPI(userDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable Long id) {

        return userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public UserDTO ReplaceUser(@PathVariable Long id, @RequestBody UserDTO updatedUserDTO) throws SQLException {
        return userService.replaceUser(id, updatedUserDTO);
    }

    // --Images with user---
    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getPostImage(@PathVariable long id)
            throws SQLException, IOException {

        Resource userImage = userService.getUserImage(id);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(userImage);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {
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
        if (!isAdmin && !authenticatedUser.get().id().equals(id)) {
            response.put("recommendedProducts", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        URI location = fromCurrentRequest().build().toUri();

        userService.createUserImage(id, location, imageFile.getInputStream(), imageFile.getSize());

        return ResponseEntity.created(location).build();

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replaceUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        userService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteUserImage(@PathVariable long id)
            throws IOException {
        
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
        if (!isAdmin && !authenticatedUser.get().id().equals(id)) {
            response.put("recommendedProducts", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        userService.deleteUserImage(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO userDTO = userService.getAuthenticatedUser();
        return ResponseEntity.ok(userDTO);
    }

}