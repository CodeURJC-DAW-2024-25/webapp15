package com.stepx.stepx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.mapper.CouponMapper;
import com.stepx.stepx.mapper.ReviewMapper;
import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.repository.CouponRepository;
import com.stepx.stepx.repository.ReviewRepository;
import com.stepx.stepx.repository.UserRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    @Autowired
    private CouponMapper couponMapper;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Optional<CouponDTO> findByCodeAndId(String couponCode, Long userId) {
        return couponRepository.findByCodeAndId(couponCode, userId)
                .map(couponMapper::toDTO);
    }
    public CouponDTO save(CouponDTO couponDto) {
        // Convert the DTO to a domain object
        Coupon coupon = couponMapper.toDomain(couponDto);
        
        // Save the coupon to the repository and get the persisted entity with ID
        Coupon savedCoupon = couponRepository.save(coupon);
        
        // Convert the saved coupon back to a DTO with the generated ID
        return couponMapper.toDTO(savedCoupon);
    }
    

      public List<CouponDTO> findAll() {
        Collection<Coupon> coupon = couponRepository.findAll();
        return couponMapper.toDTOs(coupon);
    }

    public CouponDTO findById(Long id) {
        Optional<Coupon> couponOptional = couponRepository.findById(id);
        return couponOptional.map(couponMapper::toDTO).orElse(null);
    }

    public Optional<CouponDTO> updateCoupon(Long id, CouponDTO couponDTO) {
        Optional<Coupon> existingCouponOptional = couponRepository.findById(id);
        
        if (existingCouponOptional.isEmpty()) {
            return Optional.empty();
        }
        
        Coupon existingCoupon = existingCouponOptional.get();
        Coupon updatedCoupon = couponMapper.toDomain(couponDTO);
        
        // Preserve the ID
        updatedCoupon.setId(id);
        
        // Save the updated coupon
        Coupon savedCoupon = couponRepository.save(updatedCoupon);
        return Optional.of(couponMapper.toDTO(savedCoupon));
    }
    

    public Optional<CouponDTO> deleteCoupon(Long id) {
        Optional<Coupon> couponOptional = couponRepository.findById(id);
        
        if (couponOptional.isEmpty()) {
            return Optional.empty();
        }
        
        Coupon coupon = couponOptional.get();
        couponRepository.delete(coupon);
        return Optional.of(couponMapper.toDTO(coupon));
    }

   
    
    
}
