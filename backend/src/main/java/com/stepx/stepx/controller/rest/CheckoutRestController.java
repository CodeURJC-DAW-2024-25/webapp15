package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.stepx.stepx.dto.*;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.service.*;


@Controller
@RequestMapping("/api/checkout")
public class CheckoutRestController {


    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to download the ticket PDF after processing the order.
     */
    @PostMapping("/downloadTicket")
    public void downloadTicket(
            @RequestParam Long orderId,
            @RequestParam String country,
            @RequestParam(required = false) String coupon,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam String phone,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {

        // Ensure the user is authenticated
        boolean isAuthenticated = request.getUserPrincipal() != null;
        if (!isAuthenticated) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged in to download the ticket");
            return;
        }

        // Retrieve user
        UserDTO userdto = getAuthenticatedUserOrThrow(request);
        Long userId = userdto.getId();

        // Retrieve the cart
        Optional<OrderShoesDTO> orderDToOptional = orderShoesService.getCartById(userId);
        if (!orderDToOptional.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        // Fill the order details
        OrderShoesDTO orderDto = orderDToOptional.get();
        orderDto.setCountry(country);
        orderDto.setFirstName(firstName);
        orderDto.setSecondName(lastName);
        orderDto.setEmail(email);
        orderDto.setAddress(address);
        orderDto.setNumerPhone(phone);
        orderDto.setState("Processed");
        orderDto.setActualDate();

        // Apply coupon discount if valid
        BigDecimal totalPrice = orderShoesService.getTotalPrice(orderDto.getId());
        if (coupon != null && !coupon.isEmpty()) {
            Optional<CouponDTO> couponDtoOptional = couponRepository.findByCodeAndId(coupon, user.getId());
            if (couponOptional.isPresent() && couponOptional.get().getUser().getId().equals(userId)) {
                BigDecimal discount = couponOptional.get().getDiscount();
                totalPrice = totalPrice.multiply(discount).abs();
                orderDto.setCuponUsed(coupon);
            } else {
                orderDto.setCuponUsed("No coupon applied");
            }
        } else {
            orderDto.setCuponUsed("No coupon applied");
        }
        orderDto.setSummary(totalPrice);
        orderShoesService.saveOrderShoes(orderDto);
        orderShoesService.processOrder(orderDto);
        // Prepare data for the PDF
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", firstName + " " + lastName);
        data.put("email", email);
        data.put("address", address);
        data.put("phone", phone);
        data.put("country", country);
        data.put("coupon", (coupon != null && !coupon.isEmpty()) ? coupon : "No coupon applied");
        data.put("date", orderDto.getDate());
        data.put("products", orderDto.getOrderItems());
        data.put("total", totalPrice);

        // Generate PDF
        byte[] pdfBytes = pdfService.generatePdfFromOrder(data);
        if (pdfBytes == null || pdfBytes.length == 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
            return;
        }

        // Send PDF as response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
        response.getOutputStream().write(pdfBytes);
    }

    

    // ======================= PRIVATE HELPER METHODS ======================= //

    /**
     * Retrieves the authenticated user from the HttpServletRequest.
     * Throws an exception if the user is not found.
     */
    private UserDTO getAuthenticatedUserOrThrow(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("No authenticated user found");
        }
        //se debe cambir el service para que devuelva un DTO
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Sets common model attributes for displaying cart information.
     */
    private void setModelAttributesForCart(OrderShoes cart, Model model) {
        // If the cart has no items, show empty cart attributes
        if (cart.getLenghtOrderShoes() == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return;
        }

        // Build lists of shoe IDs and sizes
        List<Long> shoeIds = cart.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId())
                .distinct()
                .collect(Collectors.toList());
        List<String> sizes = cart.getOrderItems().stream()
                .map(OrderItem::getSize)
                .distinct()
                .collect(Collectors.toList());

        // Obtain stocks in a single query
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);

        boolean stocksAvailable = true;
        List<Map<String, Object>> cartItems = new ArrayList<>();

        // Build a list of cart items with stock information
        for (OrderItem orderItem : cart.getOrderItems()) {
            Shoe shoe = orderItem.getShoe();
            String stockKey = shoe.getId() + "_" + orderItem.getSize();
            boolean stockAvailable = stockMap.getOrDefault(stockKey, 0) > 0;
            stocksAvailable = stocksAvailable && stockAvailable;

            Map<String, Object> item = new HashMap<>();
            item.put("id", shoe.getId());
            item.put("name", shoe.getName());
            item.put("price", shoe.getPrice());
            item.put("quantity", orderItem.getQuantity());
            item.put("size", orderItem.getSize());
            item.put("id_orderItem", orderItem.getId());
            item.put("stock", stockAvailable);

            cartItems.add(item);
        }

        // Calculate total excluding out-of-stock items
        BigDecimal total = orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());
        if (total == null) {
            total = BigDecimal.ZERO;
        }

        // Set the model attributes
        model.addAttribute("stocskAvailable", stocksAvailable);
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());
    }

}
