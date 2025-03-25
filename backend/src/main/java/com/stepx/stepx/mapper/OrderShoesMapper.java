package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.model.OrderShoes;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, UserMapper.class,CouponMapper.class})
public interface OrderShoesMapper {
    
    @Mapping(target = "userId", source = "user.id")
    OrderShoesDTO toDTO(OrderShoes orderShoes);
    
    @Mapping(target = "user.id", source = "userId")
    OrderShoes toDomain(OrderShoesDTO orderShoesDTO);

    List<OrderShoesDTO> toDTOs(Collection<OrderShoes> orderShoes);

}

