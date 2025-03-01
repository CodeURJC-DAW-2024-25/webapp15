
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
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;

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
public class GeneralController { // todas las solicitudes "/...." son con el controlador

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



    @GetMapping("/index")
    public String showIndex(Model model, HttpServletRequest request) {
        boolean isAuthenticated = request.getUserPrincipal() != null;

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName(); //cambiar a id
            User user = userRepository.findByUsername(username).orElseThrow();
            
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN")); //Creamos variable boleana que verifica si es admin
            model.addAttribute("isAuthenticatedAndNotAdmin", !request.isUserInRole("ROLE_ADMIN"));
        }

        return "index"; // nombre de la plantilla Mustache sin la extensión .html
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        model.addAttribute("isAuthenticated", user != null);
        return "/index";  // Redirige a la página principal
    }

    @GetMapping("/login-success")
    public String loginSuccess(HttpServletRequest request, Model model) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("isAuthenticated", request.getUserPrincipal() != null);
        return "/index";  // Redirige a la página principal
    }


    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/styles")
    public String showStyles(Model model) {
        return "styles"; // nombre de la plantilla Mustache sin la extensión .html
    }
 

    @GetMapping("/register-user")
    public String showRegisterUser(Model model) {
        return "register-user";
    }

    //Con este mapping recuperamos datos y elementos que solo pertenezcan a admin

    @GetMapping("/admin-pannel")
    public String showAdminPanel(Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();

        User user = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("admin", request.isUserInRole("ADMIN"));

        return "admin-pannel"; 
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProduct(Model model, @PathVariable Long id) {
        model.addAttribute("product", productsService.getProductById(id));
        return "edit-product";
    }

    @GetMapping("/create-product")
    public String showCreate(Model model) {
        return "create-product";

    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        return "checkout";

    }


}
