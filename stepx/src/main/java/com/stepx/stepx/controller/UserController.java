package com.stepx.stepx.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stepx.stepx.model.User;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.service.EmailService;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ShoeSizeStockService;
import com.stepx.stepx.service.UserService;
import com.stepx.stepx.repository.UserRepository;

import jakarta.mail.MessagingException;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private ShoeService shoeService;
    
    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

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

    @GetMapping("/cart")
    public String getMethodName(@RequestParam Long id_user, Model model) {
        // Existing cart code
        Optional<User> usergetted = userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            System.out.println("el usuario buscado no existe");
        }
        User user = usergetted.get();

        // verify if the user has a cart or not
        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(id_user);
        OrderShoes cart;
        if(cart_Optional.isPresent()) {
            cart = cart_Optional.get();
            if(cart.getLenghtOrderShoes() == 0) {
                model.addAttribute("setSubtotal", false);
            } else {
                List<Map<String,Object>> cartItems = new ArrayList<>();
                for(OrderItem orderItem : cart.getOrderItems()) {
                    Map<String,Object> item = new HashMap<>();
                    item.put("id", orderItem.getShoe().getId());
                    item.put("name", orderItem.getShoe().getName());
                    item.put("price", orderItem.getShoe().getPrice());
                    item.put("quantity", orderItem.getQuantity());
                    item.put("size", orderItem.getSize());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal", true);
                model.addAttribute("total", cart.getTotalPrice());
                model.addAttribute("cartItems", cartItems);
            }
        } else {
            cart = orderShoesService.createCartForUser(user);
            model.addAttribute("setSubtotal", false);
        }
        
        // Add user ID to the model for the coupon button
        model.addAttribute("userId", id_user);
        
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
            
            // This will either send a real email or log a mock email based on the configuration
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
    
    // Redirect back to the cart page
    return "index";
}
}