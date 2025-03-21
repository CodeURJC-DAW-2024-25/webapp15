
package com.stepx.stepx.controller.web;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.CouponRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GeneralController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

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
    public void addAttributes(Model model, HttpServletRequest request) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("showError", false);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());

        model.addAttribute("headerName", csrfToken.getHeaderName());

        if (isAuthenticated) {
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

        // Catch the error parameter
        String error = request.getParameter("error");
        model.addAttribute("loginError", error != null);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                // get recommended shoes
                List<Shoe> recommendedShoes = orderItemService.getRecommendedShoesForUser(user.getId(), 10);

                if (recommendedShoes.isEmpty()) {
                    model.addAttribute("recommendedShoes", false);
                    model.addAttribute("hasRecommendedShoes", false);
                } else {
                    model.addAttribute("recommendedShoes", recommendedShoes);
                    model.addAttribute("hasRecommendedShoes", true);
                }
            }
        }

        // Getting mbest seller products
        List<Shoe> bestSellingShoes = orderItemService.getBestSellingShoes(10);
        if (bestSellingShoes.isEmpty()) {
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
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        model.addAttribute("isAuthenticated", request.getUserPrincipal() != null);
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // Load all orders from user
        List<OrderShoes> orderShoes = orderShoesService.getOrderShoesFinishedByUserId(user.getId());
        if (orderShoes.size() == 0) {
            model.addAttribute("orders", false);
            model.addAttribute("hasmoreorders", false);
        } else {
            List<OrderShoes> displayedOrders = orderShoes.stream().limit(5).toList();
            model.addAttribute("orders", displayedOrders);
            if (orderShoes.size() > 5) {
                model.addAttribute("hasmoreorders", true);
            } else {
                model.addAttribute("hasmoreorders", false);
            }
        }

        // Get monthly spending data for the user
        List<Map<String, Object>> monthlySpending = orderShoesRepository.getMonthlySpendingByUserId(user.getId());

        // Prepare data for the chart
        Map<String, Object> chartData = new HashMap<>();
        String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
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

    @GetMapping("/profile/orders")
    public String loadMoreOrders(@RequestParam int page, HttpServletRequest request, Model model) {
        User user = userService.findUserByUserName(request.getUserPrincipal().getName()).orElseThrow();
        Long userId = user.getId();
        List<OrderShoes> nextOrders = orderShoesService.getPagedOrdersByUserId(page, userId);
        if (nextOrders.size() == 0) {
            model.addAttribute("oders", false);
            return "partials/ordersProfile";
        }
        model.addAttribute("orders", nextOrders);
        return "partials/ordersProfile";
    }


    @GetMapping("/register-user")
    public String showRegisterUser(Model model) {
        return "register-user";
    }
    // Recovering data and elements to show to admin users

    // @GetMapping("/admin-pannel")
    // public String showAdminPanel(Model model, HttpServletRequest request) {
    //     if (request.getUserPrincipal() == null || !request.isUserInRole("ROLE_ADMIN")) {
    //         return "redirect:/errorPage?errorType=notValidPage"; // Redirigir a la página principal si no es admin
    //     }
    //     model.addAttribute("admin", true);

    //     return "admin-pannel";
    // }

    @GetMapping("/create-product")
    public String showCreate(Model model, HttpServletRequest request) {
        boolean admin = request.isUserInRole("ROLE_ADMIN");
        if (!admin) {
            return "redirect:/errorPage?errorType=notValidPage";
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
            redirectAttributes.addFlashAttribute("error", "❌ Emails do not match.");
            return "redirect:/register-user";
        }

        // Verigy email and user exists
        if (userRepository.findByUsername(username).isPresent() /** || userRepository.findByEmail(email).isPresent() */
        ) {
            redirectAttributes.addFlashAttribute("error", "❌ Username is already in use.");
            return "redirect:/register-user";
        }

        // Code the password
        String encodedPassword = passwordEncoder.encode(password);

        // Create new user
        User newUser = new User(username, email, encodedPassword, null, "USER");
        newUser.setLastName(lastName);
        newUser.setFirstname(firstName);
        // load default image
        Blob defaultUserImage;
        try {
            Resource resource = new ClassPathResource("static/images/defaultProfilePicture.jpg");
            if (!resource.exists()) {
                defaultUserImage = null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] imageBytes = inputStream.readAllBytes();
                defaultUserImage = new SerialBlob(imageBytes);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            defaultUserImage = null;
        }
        newUser.setImageUser(defaultUserImage);
        // Saving in data
        userRepository.save(newUser);

        Coupon coupon = new Coupon();
        coupon.setCode("STEPXDISCOUNT10");
        coupon.setDiscount(new BigDecimal("0.9")); // Representing a discount of 10%
        coupon.setUser(newUser);
        couponRepository.save(coupon);
        return "redirect:/index";
    }

    @GetMapping("/errorPage")
    public String errorShow(Model model, @RequestParam(value = "errorType", required = false) String errorType) {
        String message = null;
        if (errorType != null) {
            if (errorType.equals("greaterId")) {
                message = "Invalid product. Not found.";
            } else if (errorType.equals("notValidPage")) {
                message = "Not a valid page.";
            }
        }

        model.addAttribute("showError", true);
        model.addAttribute("error", message);
        return "errorPage";
    }

}
