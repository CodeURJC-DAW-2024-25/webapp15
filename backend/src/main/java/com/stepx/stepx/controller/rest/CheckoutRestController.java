package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.model.*;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.service.*;

@Controller
@RequestMapping("/api/checkout")
public class CheckoutRestController {

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private ShoeService shoeService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    /**
     * Method to download the ticket PDF after processing the order.
     */
    @PostMapping("/downloadTicket")
    public ResponseEntity<byte[]> downloadTicket(
            @RequestParam Long orderId,
            @RequestParam String country,
            @RequestParam(required = false) String coupon,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam String phone) {

        // Ensure the user is authenticated
        UserDTO userDTO = userService.getAuthenticatedUser();
        if (userDTO == null) {
            return ResponseEntity.status(403).body("You must be logged in to download the ticket".getBytes());
        }

        // Retrieve the cart
        Optional<OrderShoesDTO> orderDToOptional = orderShoesService.getCartById(userDTO.id());
        if (!orderDToOptional.isPresent()) {
            return ResponseEntity.status(404).body("Order not found".getBytes());
        }

        OrderShoesDTO orderDto = orderDToOptional.get();

        // Apply coupon discount if valid
        BigDecimal totalPrice = orderShoesService.getTotalPrice(orderDto.id());
        String couponString = "No coupon applied";
        if (coupon != null && !coupon.isEmpty()) {
            Optional<CouponDTO> couponDtoOptional = couponService.findByCodeAndId(coupon, userDTO.id());
            if (couponDtoOptional.isPresent() && couponDtoOptional.get().userId().equals(userDTO.id())) {
                BigDecimal discount = couponDtoOptional.get().discount();
                totalPrice = totalPrice.multiply(discount).abs();
                couponString = coupon;
            }
        }

        // Fill order details
        orderDto = OrderShoesService.fillDetailsOrder(orderDto, userDTO.id(), country, coupon, firstName, lastName, email, address, phone, couponString, totalPrice);
        orderShoesService.saveOrderShoes(orderDto);
        orderShoesService.processOrder(orderDto);

        // Prepare data for the PDF
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", firstName + " " + lastName);
        data.put("email", email);
        data.put("address", address);
        data.put("phone", phone);
        data.put("country", country);
        data.put("coupon", couponString);
        data.put("date", orderDto.date());
        data.put("products", orderDto.orderItems());
        data.put("total", totalPrice);

        // Generate PDF
        byte[] pdfBytes = pdfService.generatePdfFromOrder(data);
        if (pdfBytes == null || pdfBytes.length == 0) {
            return ResponseEntity.status(500).body("Error generating PDF".getBytes());
        }

        // Send PDF as response
        return ResponseEntity.ok()
                             .header("Content-Disposition", "attachment; filename=ticket.pdf")
                             .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                             .body(pdfBytes);
    }

    /**
     * Method to apply a coupon code and recalculate the total.
     */
    @PostMapping("/applyCoupon")
    public ResponseEntity<Map<String, Object>> applyCoupon(@RequestParam String coupon) {

        UserDTO userDTO = userService.getAuthenticatedUser();
        if (userDTO == null) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", "You must be logged in"));
        }

        Optional<OrderShoesDTO> orderDtoOptional = orderShoesService.getCartById(userDTO.id());
        if (orderDtoOptional.isPresent()) {
            OrderShoesDTO orderDto = orderDtoOptional.get();
            Optional<CouponDTO> couponOptional = couponService.findByCodeAndId(coupon, userDTO.id());

            if (couponOptional.isPresent()) {
                BigDecimal discount = couponOptional.get().discount();
                BigDecimal totalPrice = orderDto.summary().multiply(discount);
                Map<String, Object> response = new HashMap<>();
                response.put("apply", true);
                response.put("summary", totalPrice);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(400).body(Collections.singletonMap("error", "Invalid coupon"));
            }
        }
        return ResponseEntity.status(404).body(Collections.singletonMap("error", "Cart not found"));
    }

    /**
     * Method to display the checkout page.
     */
    @GetMapping("/user")
public ResponseEntity<Map<String, Object>> showCheckout() {

    // Get the authenticated user
    UserDTO userDTO = userService.getAuthenticatedUser();
    if (userDTO == null) {
        return ResponseEntity.status(403).body(Collections.singletonMap("error", "User not authenticated"));
    }

    // Retrieve cart
    Optional<OrderShoesDTO> cartOptional = orderShoesService.getCartById(userDTO.id());
    if (cartOptional.isEmpty()) {
        return ResponseEntity.status(404).body(Collections.singletonMap("error", "Cart not found"));
    }

    // Retrieve the cart details
    OrderShoesDTO cart = cartOptional.get();

    // Set up cart response
    Map<String, Object> cartResponse = new HashMap<>();
    cartResponse.put("cartId", cart.id());
    cartResponse.put("userId", userDTO.id());
    cartResponse.put("total", cart.summary()); // Total summary, could be modified to reflect discount or other
    cartResponse.put("items", cart.orderItems());
    cartResponse.put("couponApplied", cart.cuponUsed());

    // Return cart information in the response
    return ResponseEntity.ok(cartResponse);
}

    /**
     * Method to delete an item from the checkout cart.
     */
    @DeleteMapping("/deleteItem/{id}")
    public ResponseEntity<Map<String, Object>> deleteItemCheckout(@PathVariable Long id) {

        UserDTO userDTO = userService.getAuthenticatedUser();
        if (userDTO == null) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", "You must be logged in"));
        }

        Optional<OrderShoesDTO> cartOptionalDto = orderShoesService.getCartById(userDTO.id());
        if (cartOptionalDto.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Cart not found"));
        }

        OrderShoesDTO cartDto = cartOptionalDto.get();

        // Delete the orderItem from the cart
        orderShoesService.deleteOrderItems(userDTO.id(), id);
        orderShoesService.saveOrderShoes(cartDto);

        // Return updated cart
        Map<String, Object> cartResponse = new HashMap<>();
        cartResponse.put("cart", cartDto);  // Replace with actual cart DTO or summary if needed
        return ResponseEntity.ok(cartResponse);
    }

    /**
     * Method to recalculate cart totals after quantity changes in the cart.
     */
    @PostMapping("/recalculate")
    public ResponseEntity<Map<String, Object>> recalculate(@RequestParam(required = false) List<Long> ids,
                                                            @RequestParam(required = false) List<Integer> quantities) {

        UserDTO userDTO = userService.getAuthenticatedUser();
        if (userDTO == null) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", "You must be logged in"));
        }

        if (ids == null || quantities == null || ids.isEmpty() || quantities.isEmpty()) {
            return ResponseEntity.status(400).body(Collections.singletonMap("error", "Invalid input"));
        }

        Optional<OrderShoesDTO> cartOptionalDto = orderShoesService.getCartById(userDTO.id());
        if (cartOptionalDto.isEmpty() || orderShoesService.getLengthOrderShoes(cartOptionalDto.get()) == 0) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Cart is empty"));
        }

        OrderShoesDTO cartDto = cartOptionalDto.get();
        // Update order items based on ids and quantities

        Map<String, Object> response = new HashMap<>();
        response.put("cart", cartDto);  // Replace with updated cart data
        return ResponseEntity.ok(response);
    }

    // ======================= PRIVATE HELPER METHODS ======================= //

    /**
     * Retrieves the authenticated user from the HttpServletRequest.
     * Throws an exception if the user is not found.
     */
    public UserDTO getAuthenticatedUser(HttpServletRequest request) {
        UserDTO userDTO = userService.getAuthenticatedUser();
        return userDTO;
    }
}
