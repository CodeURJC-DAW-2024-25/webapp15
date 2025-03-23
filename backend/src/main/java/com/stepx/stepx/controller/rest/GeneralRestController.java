package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.mapper.CouponMapper;
import com.stepx.stepx.mapper.UserMapper;
import com.stepx.stepx.repository.CouponRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.UserService;

@RestController
@RequestMapping("/api")
public class GeneralRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private OrderShoesRepository orderShoesRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserMapper userMapper;

    // @GetMapping("/user")
    // public Map<String, Object> getUserData(HttpServletRequest request) {
    //     Map<String, Object> response = new HashMap<>();
    //     boolean isAuthenticated = request.getUserPrincipal() != null;

    //     response.put("isAuthenticated", isAuthenticated);

    //     if (isAuthenticated) {
    //         String username = request.getUserPrincipal().getName();
    //         User user = userRepository.findByUsername(username).orElse(null);

    //         if (user != null) {
    //             response.put("id", user.getId());
    //             response.put("username", user.getUsername());
    //             response.put("email", user.getEmail());
    //             response.put("lastName", user.getLastName());
    //             response.put("firstName", user.getFirstName());
    //             response.put("admin", request.isUserInRole("ROLE_ADMIN"));
    //         }
    //     }
    //     return response;
    // }

    // @GetMapping("/shoes/recommended")
    // public List<Shoe> getRecommendedShoes(HttpServletRequest request) {
    //     if (request.getUserPrincipal() == null) return List.of();
    //     String username = request.getUserPrincipal().getName();
    //     Optional<User> user = userRepository.findByUsername(username);
    //     return user.map(value -> orderItemService.getRecommendedShoesForUser(value.getId(), 10)).orElse(List.of());
    // }

    // @GetMapping("/shoes/best-selling")
    // public List<Shoe> getBestSellingShoes() {
    //     return orderItemService.getBestSellingShoes(10);
    // }

    // @GetMapping("/profile")
    // public Map<String, Object> getUserProfile(HttpServletRequest request) throws JsonProcessingException {
    //     Map<String, Object> response = new HashMap<>();
    //     String username = request.getUserPrincipal().getName();
    //     User user = userRepository.findByUsername(username).orElseThrow();

    //     List<OrderShoes> orderShoes = orderShoesService.getOrderShoesFinishedByUserId(user.getId());
    //     response.put("orders", orderShoes);

    //     List<Map<String, Object>> monthlySpending = orderShoesRepository.getMonthlySpendingByUserId(user.getId());
    //     Double[] spendingData = new Double[12];

    //     for (int i = 0; i < 12; i++) {
    //         spendingData[i] = 0.0;
    //     }

    //     for (Map<String, Object> entry : monthlySpending) {
    //         int monthIndex = Integer.parseInt((String) entry.get("month")) - 1;
    //         spendingData[monthIndex] = ((Number) entry.get("total_spent")).doubleValue();
    //     }

    //     response.put("spendingData", spendingData);
    //     return response;
    // }

    // @PostMapping("/profile/update")
    // public Map<String, String> updateProfile(
    //         @RequestParam String firstName,
    //         @RequestParam String lastName,
    //         @RequestParam String username,
    //         @RequestParam String email,
    //         HttpServletRequest request) {

    //     Map<String, String> response = new HashMap<>();
    //     response.put("message", "Profile updated successfully.");
    //     return response;
    // }

    // @GetMapping("/profile/orders")
    // public List<OrderShoes> getMoreOrders(@RequestParam int page, HttpServletRequest request) {
    //     User user = userService.findUserByUserName(request.getUserPrincipal().getName()).orElseThrow();
    //     return orderShoesService.getPagedOrdersByUserId(page, user.getId());
    // }

    private String convertToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    @PostMapping("/register")
    public Map<String, String> createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String emailRepeated,
            @RequestParam String password,
            @RequestParam String lastName,
            @RequestParam String firstName) {

        Map<String, String> response = new HashMap<>();

        if (!email.equals(emailRepeated)) {
            response.put("error", "❌ Emails do not match.");
            return response;
        }

        if (userRepository.findByUsername(username).isPresent()) {
            response.put("error", "❌ Username is already in use.");
            return response;
        }

        String encodedPassword = passwordEncoder.encode(password);
        try {
            Resource resource = new ClassPathResource("static/images/defaultProfilePicture.jpg");
            String image = (resource.exists() && resource.isReadable()) ? convertToBase64((MultipartFile) resource) : null;
            
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    UserDTO newUser = new UserDTO(null,image,firstName,lastName,List.of("USER"),username, email,null, encodedPassword);
                    CouponDTO coupon = new CouponDTO(null, "STEPXDISCOUNT10", new BigDecimal("0.9"), newUser.id());
                    userRepository.save(userMapper.toDomain(newUser));
                    couponRepository.save(couponMapper.toDomain(coupon));
            
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


       
        response.put("message", "User registered successfully.");
        return response;
    }

@GetMapping("/errorPage")
    public ResponseEntity<Object> errorShow(@RequestParam(value = "errorType", required = false) String errorType) {
        String message = null;

        if (errorType != null) {
            if (errorType.equals("greaterId")) {
                message = "Invalid product. Not found.";
            } else if (errorType.equals("notValidPage")) {
                message = "Not a valid page.";
            }
        }

        if (message == null) {
            message = "An unexpected error occurred.";
        }

        // Puedes usar un Map o una clase personalizada para estructurar la respuesta
        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
    }

    // Clase para estructurar el mensaje de error
    public static class ErrorResponse {
        private String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

}
