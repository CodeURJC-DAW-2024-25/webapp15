package com.stepx.stepx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.mapper.CouponMapper;
import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.repository.CouponRepository;

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
    return couponRepository.findById(id)
            .map(couponMapper::toDTO)
            .orElseThrow(() -> new NoSuchElementException("Coupon with ID " + id + " not found"));
}

    public Optional<CouponDTO> updateCoupon(Long id, CouponDTO couponDTO) {
        Optional<Coupon> existingCouponOptional = couponRepository.findById(id);
        
        if (existingCouponOptional.isEmpty()) {
            return Optional.empty();
        }
        
        Coupon updatedCoupon = couponMapper.toDomain(couponDTO);
        
        // Preserve the ID
        updatedCoupon.setId(id);
        
        // Save the updated coupon
        Coupon savedCoupon = couponRepository.save(updatedCoupon);
        return Optional.of(couponMapper.toDTO(savedCoupon));
    }
    

    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Coupon with ID " + id + " not found"));
        
        couponRepository.delete(coupon);
    }
   
    
    
}
