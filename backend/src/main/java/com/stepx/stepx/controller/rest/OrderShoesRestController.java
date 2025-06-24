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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.model.*;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.service.*;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;






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
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    
    @Autowired
    private PdfService pdfService;

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

    //get cart by user id
    @GetMapping("/User/{userId}/Cart")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        Optional<OrderShoesDTO> existingCart = orderShoesService.getCartById(userId);

        if (existingCart.isPresent()) {
            return ResponseEntity.ok(existingCart.get());
        }
    
        UserDTO userOptional = userService.findUserById(userId);
        if (userOptional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    
        OrderShoesDTO newCart = orderShoesService.createCartForUser(userOptional);
        return ResponseEntity.ok(newCart);
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order item is empty");

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

    @GetMapping("/User/{userId}/Coupon")
    public ResponseEntity<BigDecimal> applyCouponToCart(
            @PathVariable Long userId,
            @RequestParam String couponCode) {

        // 1. Buscar carrito
        Optional<OrderShoesDTO> cartOptional = orderShoesService.getCartById(userId);

        if (cartOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderShoesDTO cart = cartOptional.get();

        // 2. Buscar el cupón válido para este usuario
        Optional<CouponDTO> couponOptional = couponService.findByCodeAndId(couponCode, userId);

        if (couponOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CouponDTO coupon = couponOptional.get();

        // 3. Calcular subtotal original
        BigDecimal subtotal = orderShoesService.getTotalPriceExcludingOutOfStock(cart.id());

        // 4. Aplicar descuento
        BigDecimal discountMultiplier = BigDecimal.valueOf(1)
                .subtract(coupon.discount().divide(BigDecimal.valueOf(100.0)));
        BigDecimal discountedTotal = subtotal.multiply(discountMultiplier);

        // Devolver el nuevo total SIN tocar base de datos
        return ResponseEntity.ok(discountedTotal);
    }


    @GetMapping("/ticket/{orderId}")
public ResponseEntity<byte[]> downloadTicket(@PathVariable Long orderId) {

    OrderShoesDTO dto = orderShoesService.getOrderById(orderId)
        .orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

    Map<String, Object> data = Map.of(
        "date",         dto.date(),
        "customerName", dto.firstName() + " " + dto.secondName(),
        "email",        dto.email(),
        "phone",        dto.numerPhone(),
        "address",      dto.address(),
        "country",      dto.country(),
        "coupon",       dto.cuponUsed(),
        "total",        dto.summary(),
        "products", dto.orderItems().stream()
            .map(i -> Map.of(
                 "shoeName", i.shoeName(),
                 "quantity", i.quantity(),
                 "price",    i.price(),
                 "size",     i.size()
            ))
            .toList()
    );

    byte[] pdfBytes = pdfService.generatePdfFromOrder(data);
    if (pdfBytes == null) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error generating PDF");
    }

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"ticket_" + orderId + ".pdf\"")
        .body(pdfBytes);
}


}
