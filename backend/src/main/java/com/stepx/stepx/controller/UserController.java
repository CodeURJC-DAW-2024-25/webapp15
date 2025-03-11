package com.stepx.stepx.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.security.RepositoryUserDetailsService;
import com.stepx.stepx.service.EmailService;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.PdfService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepositoryUserDetailsService repositoryUserDetailsService;

    @GetMapping("/cart")
    public String getCartUser(HttpServletRequest request, Model model) {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new RuntimeException("User not found");
        }

        // Existing cart code
        Optional<User> usergetted = userService.findUserByUserName(principal.getName());
        if (!usergetted.isPresent()) {
            throw new RuntimeException("User not registered");
        }
        User user = usergetted.get();

        // verify if the user has a cart or not
        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(user.getId());
        OrderShoes cart;

        if (cart_Optional.isPresent()) {
            cart = cart_Optional.get();
            if (cart.getLenghtOrderShoes() == 0) {
                model.addAttribute("setSubtotal", false);
                model.addAttribute("empty", true);
            } else {
                List<Map<String, Object>> cartItems = new ArrayList<>();
                for (OrderItem orderItem : cart.getOrderItems()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", orderItem.getShoe().getId());
                    item.put("name", orderItem.getShoe().getName());
                    item.put("price", orderItem.getShoe().getPrice());
                    item.put("quantity", orderItem.getQuantity());
                    item.put("size", orderItem.getSize());
                    item.put("id_orderItem", orderItem.getId());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal", true);
                model.addAttribute("total", cart.getTotalPrice());
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("empty", false);
            }
        } else {
            cart = orderShoesService.createCartForUser(user);
            model.addAttribute("setSubtotal", false);
            model.addAttribute("empty", true);
        }

        return "partials/quick-view-cart-modal";
    }

    @GetMapping("/send-coupon")
    public String sendCouponEmail(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String toEmail = user.getEmail();
                String subject = "Your Special Coupon from StepX";

                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("username", user.getUsername());

                // This will either send a real email or log a mock email based on the
                // configuration
                emailService.sendEmail(toEmail, subject, templateModel);

                redirectAttributes.addAttribute("sent", "true");
                redirectAttributes.addAttribute("message", "Coupon sent to your email: " + toEmail);
            } else {
                redirectAttributes.addAttribute("sent", "false");
                redirectAttributes.addAttribute("message", "User not found");
            }
        } catch (Exception e) {
            redirectAttributes.addAttribute("sent", "false");
            redirectAttributes.addAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/index";
    }

    @GetMapping("/orderItems")
    public String getOrderItems(@RequestParam Long id_order, Model model) {
        List<OrderItem> orderItemsList = orderItemService.getOrderItemsByOrderId(id_order);
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItem orderItem : orderItemsList) {
            Shoe shoe = orderItem.getShoe();
            Map<String, Object> item = new HashMap<>();
            item.put("id_item", shoe.getId());
            item.put("name", shoe.getName());
            item.put("price", shoe.getPrice());
            item.put("quantity", orderItem.getQuantity());
            item.put("size", orderItem.getSize());
            cartItems.add(item);
        }

        model.addAttribute("cartItems", cartItems);
        return "partials/profileOrderItems";
    }

    @PostMapping("/upload-profile-image")
    public String uploadProfilePicture(@RequestParam(required = false) MultipartFile imageUser,
            HttpServletRequest request, Model model) throws IOException, SQLException {
        User user = userService.findUserByUserName(request.getUserPrincipal().getName()).orElseThrow();

        if (imageUser == null) {
            return "do not found an image to load";
        }

        if (imageUser != null && !imageUser.isEmpty()) {
            user.setImageUser(new SerialBlob(imageUser.getBytes()));
        }

        userService.saveUser(user);
        model.addAttribute("user", user);

        return "partials/userImage";
    }

    @PostMapping("/updateInformation")
    public String updateInformation(Model model, HttpServletRequest request,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String email) {

        User user = userService.findUserByUserName(request.getUserPrincipal().getName()).orElseThrow();
        // if username already exists
        Optional<User> existingUser = userService.findUserByUserName(username);
        if (existingUser.isPresent() && !existingUser.get().getUsername().equals(user.getUsername())) {
            User tempUser = new User();// to mantain the information of the form
            tempUser.setFirstname(firstName);
            tempUser.setLastName(lastName);
            tempUser.setUsername(username);
            tempUser.setEmail(email);
            model.addAttribute("user", tempUser);
            model.addAttribute("uniqueUserName", false);
            return "partials/newInformationUser";
        }

        boolean updated = false;

        if (!user.getFirstName().equals(firstName)) {
            user.setFirstname(firstName);
            updated = true;
        }
        if (!user.getLastName().equals(lastName)) {
            user.setLastName(lastName);
            updated = true;
        }
        if (!user.getEmail().equals(email)) {
            user.setEmail(email);
            updated = true;
        }
        if (!user.getUsername().equals(username)) {
            user.setUsername(username);
            updated = true;
        }

        if (updated) {
            userService.saveUser(user);
        }

        if (!user.getUsername().equals(request.getUserPrincipal().getName())) {
            updateUserAuthentication(user);
        }

        Optional<User> userUpdated = userService.findUserByUserName(username);
        model.addAttribute("user", userUpdated.get());
        model.addAttribute("uniqueUserName", true);

        return "partials/newInformationUser";
    }

    private void updateUserAuthentication(User updatedUser) {
        try {
            UserDetails userDetails = repositoryUserDetailsService.loadUserByUsername(updatedUser.getUsername());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } catch (UsernameNotFoundException e) {
           throw new RuntimeException("User not found");
        }
    }

    @PostMapping("/downloadTicket/{orderId}")
    public void downloadTicketFromprofile(
            @PathVariable Long orderId,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged in to download the ticket");
            return;
        }

        // 2️⃣ Get the `UserDetails` object
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3️⃣ Get the user from the database (using
        // `userDetails.getUsername()` if you need to look it up)
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (!userOptional.isPresent()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }

        // 4️⃣ Search for the order by ID and validate that it belongs to the
        // authenticated user
        Optional<OrderShoes> orderOptional = orderShoesService.getOrderById(orderId);
        if (!orderOptional.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        OrderShoes order = orderOptional.get();
        // Prearing data to send the template
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", order.getFirstName() + " " + order.getSecondName());
        data.put("email", order.getEmail());
        data.put("address", order.getAddress());
        data.put("phone", order.getNumerPhone());
        data.put("country", order.getCountry());
        data.put("coupon", order.getCuponUsed() != null ? order.getCuponUsed() : "No coupon applied");
        data.put("date", order.getDate());
        data.put("products", order.getOrderItems());
        data.put("total", order.getSummary());

        byte[] pdfBytes = pdfService.generatePdfFromOrder(data);

        if (pdfBytes == null || pdfBytes.length == 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
        response.getOutputStream().write(pdfBytes);
    }

}