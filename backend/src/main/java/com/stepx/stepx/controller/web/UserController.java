package com.stepx.stepx.controller.web;

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

import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.security.RepositoryUserDetailsService;
import com.stepx.stepx.service.EmailService;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.PdfService;
import com.stepx.stepx.service.ShoeService;
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
    private ShoeService shoeService;

    @Autowired
    private RepositoryUserDetailsService repositoryUserDetailsService;

    @GetMapping("/cart")
    public String getCartUser(HttpServletRequest request, Model model) {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new RuntimeException("User not found");
        }

        // Existing cart code
        Optional<UserDTO> usergetted = userService.findUserByUserName(principal.getName());
        if (!usergetted.isPresent()) {
            throw new RuntimeException("User not registered");
        }
        
        UserDTO user = usergetted.get();

        Optional<OrderShoesDTO> cart_Optional = orderShoesService.getCartById(user.id());

        OrderShoesDTO cart;

        if (cart_Optional.isPresent()) {//if cart aready exists
            cart = cart_Optional.get();//cart in dto format

            if (orderShoesService.getLengthOrderShoes(cart) == 0) {//if its empty
                model.addAttribute("setSubtotal", false);
                model.addAttribute("empty", true);
            } else {
                List<Map<String, Object>> cartItems = new ArrayList<>();

                for (OrderItemDTO orderItem : cart.orderItems()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", orderItem.shoeId());
                    item.put("name", orderItem.shoeName());
                    item.put("price", orderItem.price());
                    item.put("quantity", orderItem.quantity());
                    item.put("size", orderItem.size());
                    item.put("id_orderItem", orderItem.id());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal", true);
                model.addAttribute("total", orderShoesService.getTotalPrice(cart.id()));
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("empty", false);
            }
        } else {//if not exists create one
            cart = orderShoesService.createCartForUser(user);
            model.addAttribute("setSubtotal", false);
            model.addAttribute("empty", true);
        }

        return "partials/quick-view-cart-modal";
    }

    @GetMapping("/send-coupon")//a dto
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
        List<OrderItemDTO> orderItemsList = orderItemService.getOrderItemsByOrderId(id_order);
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItemDTO orderItem : orderItemsList) {
            System.out.println("información de las orden: "+orderItem);
            Map<String, Object> item = new HashMap<>();
            item.put("id", orderItem.shoeId());
            item.put("shoeName", orderItem.shoeName());
            item.put("price", orderItem.price());
            item.put("quantity", orderItem.quantity());
            item.put("size", orderItem.size());
            cartItems.add(item);
        }

        model.addAttribute("cartItems", cartItems);
        return "partials/profileOrderItems";
    }

    @PostMapping("/upload-profile-image")//a dto
    public String uploadProfilePicture(@RequestParam(required = false) MultipartFile imageUser,HttpServletRequest request, Model model) throws IOException, SQLException {

    
        UserDTO UserDto =userService.updateUserImage(request.getUserPrincipal().getName(), imageUser);

        model.addAttribute("user", UserDto);

        return "partials/userImage";
    }

    @PostMapping("/updateInformation")
    public String updateInformation(Model model, HttpServletRequest request,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String email) {
    
        // Get the authenticated user
        String authenticatedUsername = request.getUserPrincipal().getName();
        UserDTO currentUserDto = userService.findUserByUserName(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // Check if the new username already exists
        Optional<UserDTO> existingUserDto = userService.findUserByUserName(username);
        if (existingUserDto.isPresent() && !existingUserDto.get().username().equals(authenticatedUsername)) {
            model.addAttribute("uniqueUserName", false); // Indicate that the username is not unique
            return "partials/newInformationUser"; // Return to the view with the error
        }
    
        // Pass the data to the service to update the user
        UserDTO updatedUserDto = userService.updateUser(currentUserDto.id(), firstName, lastName, username, email);
        System.out.println("usuario actualizado: "+updatedUserDto);
        
        // Update authentication if the username has changed
        if (!currentUserDto.username().equals(username)) {
            updateUserAuthentication(updatedUserDto);
        }
    
        // Add the updated user to the model
        model.addAttribute("user", updatedUserDto);
        model.addAttribute("uniqueUserName", true);
    
        return "partials/newInformationUser";
    }

    private void updateUserAuthentication(UserDTO updatedUserDto) {
        try {
            UserDetails userDetails = repositoryUserDetailsService.loadUserByUsername(updatedUserDto.username()); //userdetails not dto because its an Spring entity
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
        Optional<UserDTO> userOptionalDto = userService.findUserByUserName(userDetails.getUsername());
        if (!userOptionalDto.isPresent()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }

        // 4️⃣ Search for the order by ID and validate that it belongs to the
        // authenticated user
        Optional<OrderShoesDTO> orderOptionalDto = orderShoesService.getOrderById(orderId);
        if (!orderOptionalDto.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        OrderShoesDTO orderDto = orderOptionalDto.get();
        // Prearing data to send the template
        Map<String, Object> data = new HashMap<>();

        data.put("customerName", orderDto.firstName() + " " + orderDto.secondName());
        data.put("email", orderDto.email());
        data.put("address", orderDto.address());
        data.put("phone", orderDto.numerPhone());
        data.put("country", orderDto.country());
        data.put("coupon", orderDto.cuponUsed() != null ? orderDto.cuponUsed() : "No coupon applied");
        data.put("date", orderDto.date());
        data.put("products", orderDto.orderItems());
        data.put("total", orderDto.summary());

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