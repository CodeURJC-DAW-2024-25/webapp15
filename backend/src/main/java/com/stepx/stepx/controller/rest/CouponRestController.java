package com.stepx.stepx.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.UserDTO;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.service.CouponService;
import com.stepx.stepx.service.EmailService;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.UserService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController

@RequestMapping("/api/v1/coupon")

public class CouponRestController {
        private final CouponService couponService;


        public CouponRestController(CouponService couponService) {
        this.couponService = couponService;
    }
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    // Obtener un cupón por ID
    @GetMapping("/{id}")
    public ResponseEntity<CouponDTO> getById(@PathVariable Long id) {
        CouponDTO coupon = couponService.findById(id);
        if (coupon == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coupon);
    }
    
    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> sendCouponEmailRest(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDTO user = userService.findUserById(userId);
            if (user != null) {
                String toEmail = user.email();
                String subject = "Your Special Coupon from StepX";

                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("username", user.username());

                emailService.sendEmail(toEmail, subject, templateModel);

                response.put("success", true);
                response.put("message", "Coupon sent to email: " + toEmail);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener todos los cupones
    @GetMapping
    public ResponseEntity<List<CouponDTO>> getAll() {
        List<CouponDTO> coupons = couponService.findAll();
        return ResponseEntity.ok(coupons);
    }

    // Crear un nuevo cupón
    @PostMapping
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO couponDTO) {
        CouponDTO savedDTO = couponService.save(couponDTO);
        if (savedDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedDTO.id())
            .toUri();

        return ResponseEntity.created(location).body(savedDTO);
    }
    
    // Actualizar un cupón
    @PutMapping("/{id}")
    public ResponseEntity<CouponDTO> updateCoupon(@PathVariable Long id, @RequestBody CouponDTO couponDTO) {
        Optional<CouponDTO> updatedCoupon = couponService.updateCoupon(id, couponDTO);
        if (updatedCoupon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCoupon.get());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // GET /api/v1/coupon/validate?userId=3&code=SUMMER20
    @GetMapping("/val")
    public ResponseEntity<?> validateCoupon(@RequestParam String code,@RequestParam Long userId) {

        return couponService
                .findByCodeAndId(code, userId) // devuelve Optional<CouponDTO>
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Coupon not found for this user"));
    }


}
