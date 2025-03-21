package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.CouponDTO;

import com.stepx.stepx.model.Coupon;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring")
public interface CouponMapper {

    CouponDTO toDTO(Coupon coupon);
    Coupon toDomain(CouponDTO couponDTO);
     List<CouponDTO> toDTOs(Collection<Coupon> coupons);
}