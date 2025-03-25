package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.ShoeDTO;
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
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    // get an order by orderId
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderShoe(@PathVariable Long orderId) {

        OrderShoesDTO order = orderShoesService.getOrderById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));
        return ResponseEntity.ok(order);
    }

    // get all orderShoes
    @GetMapping("/All")
    public ResponseEntity<?> getAllOrderShoes() {
        List<OrderShoesDTO> orderList = orderShoesService.getAll();
        if (orderList.isEmpty()) {
            throw new NoSuchElementException("No OrderShoes available");
        }
        return ResponseEntity.ok(orderList);
    }
    
    //get orders by user id
    @GetMapping("/User/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderShoesDTO> orderShoesList = orderShoesService.findOrdersByUserId(userId);
        if (orderShoesList.isEmpty()) {
            throw new NoSuchElementException("No orders found for user ID " + userId);
        }
        return ResponseEntity.ok(orderShoesList);
    }
    
    //get paged orderShoes
    @GetMapping()
    public ResponseEntity<?> getPaginatedOrderShoes(@RequestParam int page, @RequestParam int size) {

        Page<OrderShoesDTO> orderShoes = orderShoesService.getPagedShoes(page,size);

        if (orderShoes.isEmpty()) {
            throw new NoSuchElementException("No OrderShoes found for the requested page.");
        }
        return ResponseEntity.ok(orderShoes);
    }
    
    //create an orderShoe for a user
    @PostMapping("/User/{userId}")
    public ResponseEntity<?> putMethodName(@PathVariable Long userId, @RequestBody OrderShoesDTO orderShoesDTO)throws IOException, SQLException {
        Optional<OrderShoesDTO> saved= orderShoesService.saveSingleOrderForUser(orderShoesDTO,userId);
        if (saved.isEmpty()) {
            throw new IllegalArgumentException("Order items are empty or not in PROCESSED state.");
        }
        URI location=ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.get().id())
        .toUri();
        return ResponseEntity.created(location).body(saved.get());
    }

    //update an Ordershoe by it id
    @PutMapping("/{orderShoeId}")
    public ResponseEntity<?> updateOrderShoe(@PathVariable Long orderShoeId, @RequestBody OrderShoesDTO orderShoesDTO) {
        Optional<OrderShoesDTO> orderShoeOptional=orderShoesService.updateOrderShoe(orderShoeId,orderShoesDTO);
        if (orderShoeOptional.isEmpty()) {
            throw new IllegalArgumentException("Order items are empty, not in PROCESSED state or user id wrong.");
        }
        return ResponseEntity.ok(orderShoeOptional.get());
    }

    //delete orderitem from user
    @DeleteMapping("/{orderId}/User/{userId}")
    public ResponseEntity<?> deleteUserOrder(@PathVariable Long orderId, @PathVariable Long userId) {

        boolean deleted = orderShoesService.deleteOrderByUser(orderId, userId);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Order not found or does not belong to user"));
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Order deleted successfully"));
        

    }
}
