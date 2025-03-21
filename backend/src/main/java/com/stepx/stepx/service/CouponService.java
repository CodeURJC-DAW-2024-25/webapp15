package com.stepx.stepx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.mapper.CouponMapper;
import com.stepx.stepx.mapper.ReviewMapper;
import com.stepx.stepx.model.Coupon;
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
    
    
}
