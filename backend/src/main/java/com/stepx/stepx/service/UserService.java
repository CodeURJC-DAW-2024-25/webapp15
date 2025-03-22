package com.stepx.stepx.service;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.mapper.UserMapper;
import com.stepx.stepx.model.User;
import com.mysql.cj.jdbc.Blob;

import com.stepx.stepx.repository.UserRepository;


@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final OrderShoesService orderShoesService;
    private final CouponService couponService;
    private final PdfService pdfService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder ,UserMapper userMapper, OrderShoesService orderShoesService, CouponService couponService , PdfService pdfService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.orderShoesService = orderShoesService;
        this.couponService = couponService;
        this.pdfService = pdfService;

    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    
    public Optional<UserDTO> findUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .map(user -> Optional.ofNullable(userMapper.toDTO(user)))
                .orElse(Optional.empty());
    }


    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getEncodedPassword());
        }
        return false;
    }

    public void registerUser(String username,String email, String password, Blob image, String... roles) {
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, email,encodedPassword, image, roles);
        userRepository.save(newUser);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public UserDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("No authenticated user found");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Buscar usuario en la base de datos
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convertir User a UserDTO
        return userMapper.toDTO(user);
    }

    public byte[] generateTicket(Long orderId, String country, String couponCode, String firstName, String lastName,
                                 String email, String address, String phone, Long userId) throws IOException {

        // Retrieve the cart
        Optional<OrderShoesDTO> orderDtoOptional = orderShoesService.getCartById(userId);
        if (!orderDtoOptional.isPresent()) {
            throw new IOException("Order not found");
        }
        OrderShoesDTO orderDto = orderDtoOptional.get();

        // Apply coupon discount if valid
        BigDecimal totalPrice = orderShoesService.getTotalPrice(orderDto.id());
        if (couponCode != null && !couponCode.isEmpty()) {
            Optional<CouponDTO> couponDtoOptional = couponService.findByCodeAndId(couponCode, userId);
            if (couponDtoOptional.isPresent() && couponDtoOptional.get().userId().equals(userId)) {
                BigDecimal discount = couponDtoOptional.get().discount();
                totalPrice = totalPrice.multiply(discount).abs();
            }
        }

        // Fill the order details
        orderDto = OrderShoesService.fillDetailsOrder(orderDto, userId, country, couponCode, firstName, lastName, email,
                address, phone, couponCode, totalPrice);
        orderShoesService.saveOrderShoes(orderDto);
        orderShoesService.processOrder(orderDto);

        // Prepare data for the PDF
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", firstName + " " + lastName);
        data.put("email", email);
        data.put("address", address);
        data.put("phone", phone);
        data.put("country", country);
        data.put("coupon", couponCode != null && !couponCode.isEmpty() ? couponCode : "No coupon applied");
        data.put("date", orderDto.date());
        data.put("products", orderDto.orderItems());
        data.put("total", totalPrice);

        return pdfService.generatePdfFromOrder(data);
    }

}