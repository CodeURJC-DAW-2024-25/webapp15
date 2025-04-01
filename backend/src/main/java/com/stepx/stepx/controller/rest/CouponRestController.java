package com.stepx.stepx.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.dto.CouponDTO;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.service.CouponService;
import com.stepx.stepx.service.OrderItemService;


import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    // Obtener un cupón por ID
    @GetMapping("/{id}")
    public ResponseEntity<CouponDTO> getById(@PathVariable Long id) {
        CouponDTO coupon = couponService.findById(id);
        if (coupon == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(coupon);
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
}
