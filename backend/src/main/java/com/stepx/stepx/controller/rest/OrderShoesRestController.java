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

@RestController
@RequestMapping("/api/v1/OrderShoes")
public class OrderShoesRestController {

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
     * Method to display the checkout page.
     */
    @GetMapping("/user/{id}/order")
    public ResponseEntity<Map<String, Object>> showCheckout(@PathVariable Long id) {

        // Retrieve cart
        Optional<OrderShoesDTO> cartOptional = orderShoesService.getCartById(id);
        if (cartOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Cart not found"));
        }

        // Retrieve the cart details
        OrderShoesDTO cart = cartOptional.get();

        // Set up cart response
        Map<String, Object> cartResponse = new HashMap<>();
        cartResponse.put("cartId", cart.id());
        cartResponse.put("userId", id);
        cartResponse.put("total", cart.summary()); // Total summary, could be modified to reflect discount or other
        cartResponse.put("items", cart.orderItems());
        cartResponse.put("couponApplied", cart.cuponUsed());

        // Return cart information in the response
        return ResponseEntity.ok(cartResponse);
    }

    /**
     * Method to delete an item from the checkout cart.
     */
    @DeleteMapping("user/{idUser}/deleteItem/{id}")
    public ResponseEntity<Map<String, Object>> deleteItemCheckout(@PathVariable Long id, @PathVariable Long idUser) {

        Optional<OrderShoesDTO> cartOptionalDto = orderShoesService.getCartById(idUser);
        if (cartOptionalDto.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Cart not found"));
        }

        OrderShoesDTO cartDto = cartOptionalDto.get();

        // Delete the orderItem from the cart
        orderShoesService.deleteOrderItems(idUser, id);
        orderShoesService.saveOrderShoes(cartDto);

        // Return updated cart
        Map<String, Object> cartResponse = new HashMap<>();
        cartResponse.put("cart", cartDto); // Replace with actual cart DTO or summary if needed
        return ResponseEntity.ok(cartResponse);
    }
}
