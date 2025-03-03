
package com.stepx.stepx.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;

import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GeneralController {

    @Autowired
    private ProductsService productsService;

    @Autowired
    private ShoeService shoeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({ "/", "/index" })
    public String showIndex(Model model, HttpServletRequest request) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElseThrow();

            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN")); // Create boolean value for admin
                                                                        

        }

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        model.addAttribute("isAuthenticated", user != null);
        return "index"; // Index, main page
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        model.addAttribute("isAuthenticated", request.getUserPrincipal() != null);

        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("imageBlob", user.getImageUser());
        ///{userId}/imageUser"
        /// /{{user.id}}/imageUser
        model.addAttribute("id", user.getId());
        return "profile"; 
    }


    @GetMapping("/profile/update")
    public String profileUpdate(
    @RequestParam("firstName") String firstName,
    @RequestParam("lastName") String lastName,
    @RequestParam("username") String username,
    @RequestParam("email") String email,HttpServletRequest request, Model model){
        
        model.addAttribute("updateBanner", true);
        return "profile";
    }

    @GetMapping("/styles")
    public String showStyles(Model model) {
        return "styles"; 
    }

    @GetMapping("/register-user")
    public String showRegisterUser(Model model) {
        return "register-user";
    }
    //Recovering data and elements to show to admin users

    @GetMapping("/admin-pannel")
    public String showAdminPanel(Model model, HttpServletRequest request) {
        if (request.getUserPrincipal() == null || !request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/index"; // Redirigir a la página principal si no es admin
        }
        model.addAttribute("admin", true);

        return "admin-pannel";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProduct(Model model, @PathVariable Long id, HttpServletRequest request) {
        model.addAttribute("product", productsService.getProductById(id));
        model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        return "edit-product";
    }

    @GetMapping("/create-product")
    public String showCreate(Model model, HttpServletRequest request) {
        model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        return "create-product";

    }

    @PostMapping("/createAccount")
    public String createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String emailRepeated,
            @RequestParam String password,
            @RequestParam String lastName,
            @RequestParam String firstName,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Validate match emails
        if (!email.equals(emailRepeated)) {
            redirectAttributes.addFlashAttribute("error", "❌ Los correos electrónicos no coinciden.");
            return "redirect:/register-user";
        }

        //Verigy email and user exists
        if (userRepository.findByUsername(username).isPresent() /** || userRepository.findByEmail(email).isPresent() */
        ) {
            redirectAttributes.addFlashAttribute("error", "❌ El nombre de usuario ya está en uso.");
            return "redirect:/register-user";
        }

        // Code the password
        String encodedPassword = passwordEncoder.encode(password);

        // Create new user
        User newUser = new User(username, email, encodedPassword, null, "USER");
        newUser.setLastName(lastName);
        newUser.setFirstname(firstName);

        // Saving in data
        userRepository.save(newUser);

        return "redirect:/index";
    }

}
