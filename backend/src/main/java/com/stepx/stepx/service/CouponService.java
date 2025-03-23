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
        Coupon coupon = couponMapper.toDomain(couponDto);
        couponRepository.save(coupon);
        return couponMapper.toDTO(coupon);
    }

      public List<CouponDTO> findAll() {
        Collection<Coupon> coupon = couponRepository.findAll();
        return couponMapper.toDTOs(coupon);
    }

    public CouponDTO findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public Optional<CouponDTO> updateCoupon(Long id, CouponDTO couponDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCoupon'");
    }

    public Optional<CouponDTO> deleteCoupon(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCoupon'");
    }

   
    
    
}
