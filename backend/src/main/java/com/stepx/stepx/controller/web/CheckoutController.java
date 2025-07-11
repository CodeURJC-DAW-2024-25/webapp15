package com.stepx.stepx.controller.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.service.*;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

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
    private UserService userService;

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
    UserDTO userdto = getAuthenticatedUser(request);
    Long userId = userdto.id();

    try {
        byte[] pdfBytes = userService.generateTicket(orderId, country, coupon, firstName, lastName, email, address, phone, userId);
        if (pdfBytes == null || pdfBytes.length == 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
            return;
        }

        // Send PDF as response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
        response.getOutputStream().write(pdfBytes);
    } catch (IOException e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
}


    /**
     * Method to apply a coupon code and recalculate the total.
     */
    @PostMapping("/applyCoupon")
public String applyCoupon(@RequestParam String coupon, HttpServletRequest request, Model model) {
    boolean isAuthenticated = request.getUserPrincipal() != null;
    if (!isAuthenticated) {
        return "redirect:/errorPage";
    }

        // Retrieve user
        UserDTO userDto = getAuthenticatedUser(request);

        // Retrieve the cart
        Optional<OrderShoesDTO> orderDtoOptional = orderShoesService.getCartById(userDto.id());
        if (orderDtoOptional.isPresent()) {
            OrderShoesDTO orderDto = orderDtoOptional.get();
            Optional<CouponDTO> couponOptional = couponService.findByCodeAndId(coupon, userDto.id());

            if (couponOptional.isPresent()) {
                BigDecimal discount = couponOptional.get().discount();
                BigDecimal totalPrice = orderDto.summary().multiply(discount);
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
        UserDTO userDto = getAuthenticatedUser(request);

        // Retrieve cart
        Optional<OrderShoesDTO> cartOptional = orderShoesService.getCartById(userDto.id());
        if (cartOptional.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocksAvailable", false);
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
        UserDTO userDto = getAuthenticatedUser(request);

        // Retrieve cart
        Optional<OrderShoesDTO> cartOptionalDto = orderShoesService.getCartById(userDto.id());
        if (cartOptionalDto.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        // Delete the orderItem from the cart
        OrderShoesDTO edited = orderShoesService.deleteOrderItems(userDto.id(), id);
        if(edited==null){
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        //orderShoesService.saveOrderShoes(cartDto);

        // Refresh the cart after deletion
        setModelAttributesForCart(edited, model);

        return "partials/checkout-itemsList";
    }

    /**
     * Method to recalculate cart totals after quantity changes in the cart.
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/recalculate")
    public String recalculate(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) List<Integer> quantities,
            Model model,
            HttpServletRequest request) {

        // Retrieve user
        UserDTO userDto = getAuthenticatedUser(request);

        // Basic checks
        if (ids == null || quantities == null || ids.isEmpty() || quantities.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocksAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        // Retrieve cart
        Optional<OrderShoesDTO> cartOptionalDto = orderShoesService.getCartById(userDto.id());
        if (cartOptionalDto.isEmpty() || orderShoesService.getLengthOrderShoes(cartOptionalDto.get()) == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocksAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        // Update order items
        OrderShoesDTO cartDto = cartOptionalDto.get();

        Map<String, Object> shoeInfo = orderShoesService.getShoeIdsAndSizes(cartDto);
        List<Long> shoeIds = (List<Long>) shoeInfo.get("shoeIds");
        List<String> sizes = (List<String>) shoeInfo.get("sizes");

        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);

        List<Long> updatedIds = new ArrayList<>();
        List<Integer> updatedQuantities = new ArrayList<>();

        // For each item in the cart, adjust quantity according to available stock
        for (OrderItemDTO orderItemDto : cartDto.orderItems()) {
            String stockKey = orderItemDto.shoeId() + "_" + orderItemDto.size();
            int availableStock = stockMap.getOrDefault(stockKey, 0);

            int index = ids.indexOf(orderItemDto.id());
            int newQuantity = (index != -1 && quantities.get(index) != null) ? quantities.get(index)
                    : orderItemDto.quantity();

            // Ensure quantity is at least 1
            if (newQuantity < 1) {
                newQuantity = orderItemDto.quantity();
            }
            // Ensure we do not exceed available stock
            if (newQuantity > availableStock) {
                newQuantity = availableStock;
            }

            updatedIds.add(orderItemDto.id());
            updatedQuantities.add(newQuantity);
        }

        // Batch update order items
        orderItemService.updateOrderItemsBatch(updatedIds, updatedQuantities);

        // Retrieve updated cart
        cartDto = orderShoesService.getCartById(userDto.id()).orElseThrow();

        // Set model attributes for the updated cart
        setModelAttributesForCart(cartDto, model);

        return "partials/checkout-itemsList";
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

    /**
     * Sets common model attributes for displaying cart information.
     */
    @SuppressWarnings("unchecked")
    private void setModelAttributesForCart(OrderShoesDTO cart, Model model) {
        int cartLength = orderShoesService.getLengthOrderShoes(cart);

        // Si el carrito no tiene artículos, mostramos atributos de carrito vacío
        if (cartLength == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return;
        }

        Map<String, Object> shoeInfo = orderShoesService.getShoeIdsAndSizes(cart);
        List<Long> shoeIds = (List<Long>) shoeInfo.get("shoeIds");
        List<String> sizes = (List<String>) shoeInfo.get("sizes");

        // Obtener stock en una sola consulta
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);

        boolean stocksAvailable = true;
        List<Map<String, Object>> cartItems = new ArrayList<>();

        System.out.println("Mapa de stocks de lo obtenido en el carrito: "+stockMap);//prueba

        // Construir lista de artículos del carrito con información de stock
        for (OrderItemDTO orderItemDto : cart.orderItems()) {
            String stockKey = orderItemDto.shoeId() + "_" + orderItemDto.size();
            boolean stockAvailable = stockMap.getOrDefault(stockKey, 0) > 0;
            stocksAvailable = stocksAvailable && stockAvailable;
            System.out.println("hay stock? "+stocksAvailable);//prueba
            Map<String, Object> item = new HashMap<>();
            item.put("id", orderItemDto.shoeId());
            item.put("name", orderItemDto.shoeName());
            item.put("price", shoeService.getPricefromShoe(shoeService.getShoeById(orderItemDto.shoeId()).get()));
            // lado
            item.put("quantity", orderItemDto.quantity());
            item.put("size", orderItemDto.size());
            item.put("id_orderItem", orderItemDto.id());
            item.put("stock", stockAvailable);

            cartItems.add(item);
        }

        // Calcular total excluyendo artículos fuera de stock
        BigDecimal total = orderShoesService.getTotalPriceExcludingOutOfStock(cart.id());
        if (total == null) {
            total = BigDecimal.ZERO;
        }

        // Establecer atributos en el modelo
        model.addAttribute("stocksAvailable", stocksAvailable);//stocskAvailable
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.id());

    }
}
