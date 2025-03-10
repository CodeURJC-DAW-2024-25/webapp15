package com.stepx.stepx.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;

import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.*;
import com.stepx.stepx.model.Shoe;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.UserService;
import com.stepx.stepx.service.ShoeSizeStockService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.stepx.stepx.service.PdfService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;

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
        System.out.println("🔹 Recibiendo solicitud para descargar ticket con ID: " + orderId);

        boolean isAuthenticated = request.getUserPrincipal() != null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated) {
            System.out.println("❌ Acceso denegado: Usuario no autenticado.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged in to download the ticket");
            return;
        }   

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (!userOptional.isPresent()) {
            System.out.println("❌ Error: Usuario no encontrado.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }
        User user = userOptional.get();
        Long userId = user.getId();

        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(userId);
        if (!orderOptional.isPresent()) {
            System.out.println("❌ Error: Orden no encontrada con ID " + orderId + " para el usuario " + userId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

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
            Optional<Coupon> couponOptional = couponRepository.findByCodeAndId(coupon,user.getId());
            if (couponOptional.isPresent() && couponOptional.get().getUser().getId().equals(userId)) {
                BigDecimal discount = couponOptional.get().getDiscount();
                totalPrice = totalPrice.multiply(discount).abs();
                order.setCuponUsed(coupon);
            }else{
                order.setCuponUsed("No coupon applied");
            }
        }else{
            order.setCuponUsed("No coupon applied");
        }
        order.setSummary(totalPrice);
        orderShoesService.saveOrderShoes(order);

        // Prepare data for the PDF
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", firstName + " " + lastName);
        data.put("email", email);
        data.put("address", address);
        data.put("phone", phone);
        data.put("country", country);
        data.put("coupon", coupon != null ? coupon : "No coupon applied"); // Include coupon info
        data.put("date", order.getDate());
        data.put("products", order.getOrderItems());
        data.put("total", totalPrice);

        // Generate PDF
        byte[] pdfBytes = pdfService.generatePdfFromOrder(data);

        if (pdfBytes == null || pdfBytes.length == 0) {
            System.out.println("❌ Error: El PDF está vacío o no se generó correctamente.");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
            return;
        }

        System.out.println("✅ PDF generado correctamente. Enviando respuesta...");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
        response.getOutputStream().write(pdfBytes);
    }

    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam String coupon, HttpServletRequest request,Model model) {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        if (!isAuthenticated) {
            return "redirect:/errorPage";
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (!userOptional.isPresent()) {  
            return "redirect:/errorPage";
        }

        User user = userOptional.get();
        Optional<Coupon> couponOptional = couponRepository.findByCodeAndId(coupon,user.getId());//get coupon
        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(user.getId());
        if (orderOptional.isPresent()) {
            if (couponOptional.isPresent()) {
                BigDecimal discount = couponOptional.get().getDiscount();
                OrderShoes order = orderOptional.get();
                BigDecimal totalPrice = order.getTotalPrice().multiply(discount);
                model.addAttribute("apply", true);
                model.addAttribute("Summary", totalPrice);
                return "partials/finalSummary";
            }else{
                model.addAttribute("error", true);
                return "partials/errorCoupon";
            }
        }
        return "redirect:/errorPage";

    }

    @GetMapping("/user")
    public String showCheckout(HttpServletRequest request, Model model) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated) {
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }
        User user = userRepository.findByUsername(request.getUserPrincipal().getName()).orElseThrow();

        if (user == null) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "checkout";
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(user.getId()); // get the cart asosiated to
                                                                                          // the id

        if (cart_Optional.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);

            return "checkout";
        }

        OrderShoes cart = cart_Optional.get();

        if (cart.getLenghtOrderShoes() == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
            model.addAttribute("id_orderShoe", cart.getId());
            return "checkout";
        }

        // ids of all of orderitems from cart
        List<Long> shoeIds = cart.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId())
                .distinct()
                .collect(Collectors.toList());
        // get all sizes list of orderitems
        List<String> sizes = cart.getOrderItems().stream()
                .map(OrderItem::getSize)
                .distinct()
                .collect(Collectors.toList());

        // getting all stocks in one optimized request
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);
        boolean stocskAvailable=true;
        // process the information
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItem orderItem : cart.getOrderItems()) {
            Shoe shoe = orderItem.getShoe();
            String stockKey = shoe.getId() + "_" + orderItem.getSize();
            boolean stockAvailable = stockMap.getOrDefault(stockKey, 0) > 0;/// if the stock is grater than 0
            stocskAvailable=stocskAvailable && stockAvailable;
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

        BigDecimal total = orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());
        model.addAttribute("stocskAvailable", stocskAvailable);
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());
        return "checkout";

    }

    @GetMapping("/deleteItem/{id}")
    public String DeleteItemCheckout(@PathVariable Long id, Model model, HttpServletRequest request) {

        User user = userRepository.findByUsername(request.getUserPrincipal().getName()).orElseThrow();
        if (user == null) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(user.getId()); // get the cart asosiated to
                                                                                          // the id

        OrderShoes cart = cart_Optional.get();
        if (cart.getLenghtOrderShoes() == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        orderShoesService.deleteOrderItems(user.getId(), id);// delete the orderitem from the cart
        orderShoesService.saveOrderShoes(cart);

        List<OrderItem> remainingItems = cart.getOrderItems();
        if (remainingItems.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        List<Long> shoeIds = remainingItems.stream()
                .map(orderItem -> orderItem.getShoe().getId())
                .distinct()
                .collect(Collectors.toList());

        // get all sizes list of orderitems
        List<String> sizes = cart.getOrderItems().stream()
                .map(OrderItem::getSize)
                .distinct()
                .collect(Collectors.toList());

        // getting all stocks in one optimized request
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);
        boolean stocskAvailable=true;
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItem orderItem : remainingItems) {
            Shoe shoe = orderItem.getShoe();
            String stockKey = shoe.getId() + "_" + orderItem.getSize();
            boolean stockAvailable = stockMap.getOrDefault(stockKey, 0) > 0;
            stocskAvailable=stocskAvailable && stockAvailable;//to verify if all the products are available
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

        BigDecimal total = orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());
        if (total == null) {
            total = BigDecimal.ZERO;
        }
        model.addAttribute("stocskAvailable", stocskAvailable);
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());

        return "partials/checkout-itemsList";
    }

    @PostMapping("/recalculate")
    public String Recalculate(@RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) List<Integer> quantities,
            Model model,
            HttpServletRequest request) {

        User user = userRepository.findByUsername(request.getUserPrincipal().getName()).orElseThrow();
        if (user == null || ids == null || quantities == null || ids.isEmpty() || quantities.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        Optional<OrderShoes> cartOptional = orderShoesService.getCartById(user.getId());
        if (!cartOptional.isPresent() || cartOptional.get().getLenghtOrderShoes() == 0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("stocskAvailable", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        OrderShoes cart = cartOptional.get();

        // getting ID's List for every product in cart
        List<Long> shoeIds = cart.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId())
                .distinct()
                .collect(Collectors.toList());

        // getting all sizes list of orderItems
        List<String> sizes = cart.getOrderItems().stream()
                .map(OrderItem::getSize)
                .distinct()
                .collect(Collectors.toList());

        // Getting all stocks in one optimized request
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds, sizes);

        System.out.println("IDs pasados por parámetros: " + ids);
        System.out.println("Cantidades pasadas por parámetro: " + quantities);
        System.out.println("Mapa con los stocks disponibles: " + stockMap);

        List<Long> updatedIds = new ArrayList<>();
        List<Integer> updatedQuantities = new ArrayList<>();

        for (OrderItem orderItem : cart.getOrderItems()) {
            String stockKey = orderItem.getShoe().getId() + "_" + orderItem.getSize();
            int availableStock = stockMap.getOrDefault(stockKey, 0);

            // obtener índice del orderItem en la lista de IDs recibidos
            int index = ids.indexOf(orderItem.getId());// se sibre entiende que existe pero es una buena comprobación

            // si el índice no existe o la cantidad es nula, usar el valor actual del
            // OrderItem
            int newQuantity = (index != -1 && quantities.get(index) != null) ? quantities.get(index)
                    : orderItem.getQuantity();

            // validar que el número sea mayor a 0
            if (newQuantity < 1) {
                newQuantity = orderItem.getQuantity();
            }

            // si el nuevo valor es mayor al stock disponible, ajustar al stock máximo
            if (newQuantity > availableStock) {
                newQuantity = availableStock;
            }

            updatedIds.add(orderItem.getId());
            updatedQuantities.add(newQuantity);
        }

        System.out.println("IDs a actualizar: " + updatedIds);
        System.out.println("Cantidades a actualizar: " + updatedQuantities);

        orderItemService.updateOrderItemsBatch(updatedIds, updatedQuantities);

        cart = orderShoesService.getCartById(user.getId()).orElseThrow();
        boolean stocskAvailable=true;
        // Procesing all products in cart
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItem orderItem : cart.getOrderItems()) {
            Shoe shoe = orderItem.getShoe();
            boolean stockAvailable = orderItem.getQuantity() > 0;
            stocskAvailable=stocskAvailable && stockAvailable;//to verify if all the products are available
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

        // Calculating new total
        BigDecimal total = orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());

        // Send data to view
        model.addAttribute("stocskAvailable", stocskAvailable);
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());

        return "partials/checkout-itemsList";
    }

}
