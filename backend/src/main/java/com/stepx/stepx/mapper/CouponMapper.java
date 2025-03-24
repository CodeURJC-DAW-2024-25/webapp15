package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.CouponDTO;

import com.stepx.stepx.model.Coupon;
import java.util.Collection;
import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring")
public interface CouponMapper {

       
      
     // Map from Coupon entity to CouponDTO
     @Mapping(source = "id",       target = "id")
     @Mapping(source = "user.id", target = "userId") // Map user.id to userId
     CouponDTO toDTO(Coupon coupon);
 
     // Map from CouponDTO to Coupon entity
     @Mapping(target = "user.id", source = "userId") // Map userId to user.id
     Coupon toDomain(CouponDTO couponDTO);
 
     // Map a collection of Coupon entities to a list of CouponDTOs
     List<CouponDTO> toDTOs(Collection<Coupon> coupons);
}