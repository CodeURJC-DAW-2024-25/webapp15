
package com.stepx.stepx.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.*;
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
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserService userService;

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private OrderShoesRepository orderShoesRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request){
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("showError", false);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());
        
        model.addAttribute("headerName", csrfToken.getHeaderName());

        if(isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            model.addAttribute("username", username);

            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

            User user = userRepository.findByUsername(username).get();

            model.addAttribute("id", user.getId());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("imageBlob", user.getImageUser());
            model.addAttribute("lastName", user.getLastName());
            model.addAttribute("firstname", user.getFirstName());
            model.addAttribute("user_id", user.getId());
    
            
        }
    }

    @GetMapping({ "/", "/index" })
    public String showIndex(Model model, HttpServletRequest request) {
        boolean isAuthenticated = request.getUserPrincipal() != null;

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).get();

            // Obtener productos recomendados
            List<Shoe> recommendedShoes = orderItemService.getRecommendedShoesForUser(user.getId(), 10);
            

            if (recommendedShoes.isEmpty()) {
                System.out.println("esta la lista  de productos recomendados esta vacia");
                model.addAttribute("recommendedShoes", false);
                model.addAttribute("hasRecommendedShoes", false);
    
            }else{
                model.addAttribute("recommendedShoes", recommendedShoes);
                model.addAttribute("hasRecommendedShoes", true);
            }
        }

        List<Shoe> bestSellingShoes = orderItemService.getBestSellingShoes(10); // Mostrar los 5 más vendidos
        if (bestSellingShoes.isEmpty()) {
            System.out.println("esta la lista  de mejores products esta vacia");
            model.addAttribute("bestSellingShoes", false);

        } else {
            model.addAttribute("bestSellingShoes", bestSellingShoes);
        }

        return "index";
    }

    

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        model.addAttribute("isAuthenticated", user != null);
        return "index";
    }

   @GetMapping("/profile")
public String profile(HttpServletRequest request, Model model) throws JsonProcessingException {
    model.addAttribute("isAuthenticated", request.getUserPrincipal() != null);
    String username = request.getUserPrincipal().getName();
    User user = userRepository.findByUsername(username).orElseThrow();

    // Load all orders from user
    List<OrderShoes> orderShoes = orderShoesService.getOrderShoesFinishedByUserId(user.getId());
    if (orderShoes.size() == 0) {
        model.addAttribute("orders", false);
    } else {
        model.addAttribute("orders", orderShoes);
    }

    // Get monthly spending data for the user
    List<Map<String, Object>> monthlySpending = orderShoesRepository.getMonthlySpendingByUserId(user.getId());

    // Prepare data for the chart
    Map<String, Object> chartData = new HashMap<>();
    String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    Double[] spendingData = new Double[12];

    // Initialize with zeros
    for (int i = 0; i < 12; i++) {
        spendingData[i] = 0.0;
    }

    // Fill in the actual spending data
    for (Map<String, Object> entry : monthlySpending) {
        String monthStr = (String) entry.get("month");
        Number amount = (Number) entry.get("total_spent");
        Double totalSpent = amount != null ? amount.doubleValue() : 0.0;

        // Convert month string to zero-based index
        int monthIndex = Integer.parseInt(monthStr) - 1;
        if (monthIndex >= 0 && monthIndex < 12) {
            spendingData[monthIndex] = totalSpent;
        }
    }

    chartData.put("labels", monthNames);
    chartData.put("data", spendingData);

    // Convert to JSON and pass to the view
    String chartDataJson = objectMapper.writeValueAsString(chartData);
    model.addAttribute("spendingData", "var spendingData = " + chartDataJson + ";");

    return "profile";
}

    @GetMapping("/profile/update")
    public String profileUpdate(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email, HttpServletRequest request, Model model) {

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
    // Recovering data and elements to show to admin users

    @GetMapping("/admin-pannel")
    public String showAdminPanel(Model model, HttpServletRequest request) {
        if (request.getUserPrincipal() == null || !request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/error-page?errorType=notValidPage"; // Redirigir a la página principal si no es admin
        }
        model.addAttribute("admin", true);

        return "admin-pannel";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProduct(Model model, @PathVariable Long id, HttpServletRequest request) {
        model.addAttribute("product", productsService.getProductById(id));
        //model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        boolean admin = request.isUserInRole("ROLE_ADMIN");
        if (!admin){
            return "redirect:/error-page?errorType=notValidPage";
        }
        return "edit-product";
    }

    @GetMapping("/create-product")
    public String showCreate(Model model, HttpServletRequest request) {
        //model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        boolean admin = request.isUserInRole("ROLE_ADMIN");
        if (!admin){
            return "redirect:/error-page?errorType=notValidPage";
        }
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

        // Verigy email and user exists
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

    @GetMapping("/error-page")
    public String login(Model model, @RequestParam(value = "errorType", required = false) String errorType){
        String message = null;
        if (errorType != null) {
            if (errorType.equals("greaterId")){
                message = "Invalid product. Not found.";
            }
            else if (errorType.equals("notValidPage")){
                message = "Not a valid page.";
            }
        }
        else{
            message = "An error occurred. Please try again.";
        }

        model.addAttribute("showError", true);
        model.addAttribute("message", message);
         return "index";
    }    

}
