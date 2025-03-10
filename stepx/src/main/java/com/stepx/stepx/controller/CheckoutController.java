package com.stepx.stepx.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.stepx.stepx.model.*;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.service.*;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private UserService userService;

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
        User user = getAuthenticatedUserOrThrow(request);
        Long userId = user.getId();

        // Retrieve the cart
        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(userId);
        if (!orderOptional.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        // Fill the order details
        OrderShoes order = orderOptional.get();
        order.setCountry(country);
        order.setFirstName(firstName);
        order.setSecondName(lastName);
        order.setEmail(email);
        order.setAddress(address);
        order.setNumerPhone(phone);
        order.setState("Processed");
        order.setActualDate();

        // Apply coupon discount if valid
        BigDecimal totalPrice = order.getTotalPrice();
        if (coupon != null && !coupon.isEmpty()) {
            Optional<Coupon> couponOptional = couponRepository.findByCodeAndId(coupon, user.getId());
            if (couponOptional.isPresent() && couponOptional.get().getUser().getId().equals(userId)) {
                BigDecimal discount = couponOptional.get().getDiscount();
                totalPrice = totalPrice.multiply(discount).abs();
                order.setCuponUsed(coupon);
            } else {
                order.setCuponUsed("No coupon applied");
            }
        } else {
            order.setCuponUsed("No coupon applied");
        }
        order.setSummary(totalPrice);
        orderShoesService.saveOrderShoes(order);
        orderShoesService.processOrder(order);
        // Prepare data for the PDF
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", firstName + " " + lastName);
        data.put("email", email);
        data.put("address", address);
        data.put("phone", phone);
        data.put("country", country);
        data.put("coupon", (coupon != null && !coupon.isEmpty()) ? coupon : "No coupon applied");
        data.put("date", order.getDate());
        data.put("products", order.getOrderItems());
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

    /**
     * Method to apply a coupon code and recalculate the total.
     */
    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam String coupon, HttpServletRequest request, Model model) {

        // Ensure the user is authenticated
        boolean isAuthenticated = request.getUserPrincipal() != null;
        if (!isAuthenticated) {
            return "redirect:/errorPage";
        }

        // Retrieve user
        User user = getAuthenticatedUserOrThrow(request);

        // Retrieve the cart
        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(user.getId());
        if (orderOptional.isPresent()) {
            OrderShoes order = orderOptional.get();
            Optional<Coupon> couponOptional = couponRepository.findByCodeAndId(coupon, user.getId());

            if (couponOptional.isPresent()) {
                BigDecimal discount = couponOptional.get().getDiscount();
                BigDecimal totalPrice = order.getTotalPrice().multiply(discount);
                model.addAttribute("apply", true);
                model.addAttribute("Summary", totalPrice);
                return "partials/finalSummary";
            } else {
                model.addAttribute("error", true);
                return "partials/errorCoupon";
            }
        }
        return "redirect:/errorPage";
    }

    /**
     * Method to display the checkout page.
     */
    @GetMapping("/user")
    public String showCheckout(HttpServletRequest request, Model model) {

        // Get CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        // Check authentication
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated) {
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        // If user is not authenticated or not found, return default checkout page
        if (!isAuthenticated) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "checkout";
        }

        // Retrieve user
        User user = getAuthenticatedUserOrThrow(request);

        // Retrieve cart
        Optional<OrderShoes> cartOptional = orderShoesService.getCartById(user.getId());
        if (cartOptional.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "checkout";
        }

        // Set model attributes for the cart
        setModelAttributesForCart(cartOptional.get(), model);

        return "checkout";
    }

    /**
     * Method to delete an item from the checkout cart.
     */
    @GetMapping("/deleteItem/{id}")
    public String deleteItemCheckout(@PathVariable Long id, Model model, HttpServletRequest request) {

        // Retrieve user
        User user = getAuthenticatedUserOrThrow(request);

        // Retrieve cart
        Optional<OrderShoes> cartOptional = orderShoesService.getCartById(user.getId());
        if (cartOptional.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        OrderShoes cart = cartOptional.get();

        // Delete the orderItem from the cart
        orderShoesService.deleteOrderItems(user.getId(), id);
        orderShoesService.saveOrderShoes(cart);

        // Refresh the cart after deletion
        setModelAttributesForCart(cart, model);

        return "partials/checkout-itemsList";
    }

    /**
     * Method to recalculate cart totals after quantity changes in the cart.
     */
    @PostMapping("/recalculate")
    public String recalculate(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) List<Integer> quantities,
            Model model,
            HttpServletRequest request) {

        // Retrieve user
        User user = getAuthenticatedUserOrThrow(request);

        // Basic checks
        if (ids == null || quantities == null || ids.isEmpty() || quantities.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        // Retrieve cart
        Optional<OrderShoes> cartOptional = orderShoesService.getCartById(user.getId());
        if (cartOptional.isEmpty() || cartOptional.get().getLenghtOrderShoes() == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        // Update order items
        OrderShoes cart = cartOptional.get();

        // Build a map to check stock
        List<Long> shoeIds = cart.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId())
                .distinct()
                .collect(Collectors.toList());
        List<String> sizes = cart.getOrderItems().stream()
                .map(OrderItem::getSize)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);

        List<Long> updatedIds = new ArrayList<>();
        List<Integer> updatedQuantities = new ArrayList<>();

        // For each item in the cart, adjust quantity according to available stock
        for (OrderItem orderItem : cart.getOrderItems()) {
            String stockKey = orderItem.getShoe().getId() + "_" + orderItem.getSize();
            int availableStock = stockMap.getOrDefault(stockKey, 0);

            int index = ids.indexOf(orderItem.getId());
            int newQuantity = (index != -1 && quantities.get(index) != null) ? quantities.get(index)
                    : orderItem.getQuantity();

            // Ensure quantity is at least 1
            if (newQuantity < 1) {
                newQuantity = orderItem.getQuantity();
            }
            // Ensure we do not exceed available stock
            if (newQuantity > availableStock) {
                newQuantity = availableStock;
            }

            updatedIds.add(orderItem.getId());
            updatedQuantities.add(newQuantity);
        }

        // Batch update order items
        orderItemService.updateOrderItemsBatch(updatedIds, updatedQuantities);

        // Retrieve updated cart
        cart = orderShoesService.getCartById(user.getId()).orElseThrow();

        // Set model attributes for the updated cart
        setModelAttributesForCart(cart, model);

        return "partials/checkout-itemsList";
    }

    // ======================= PRIVATE HELPER METHODS ======================= //

    /**
     * Retrieves the authenticated user from the HttpServletRequest.
     * Throws an exception if the user is not found.
     */
    private User getAuthenticatedUserOrThrow(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("No authenticated user found");
        }

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
