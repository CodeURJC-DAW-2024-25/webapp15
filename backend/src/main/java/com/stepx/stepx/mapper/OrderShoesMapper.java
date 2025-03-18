package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring", uses = {ShoeMapper.class, CouponMapper.class, OrderItemMapper.class, UserMapper.class})
public interface OrderShoesMapper {
    
    OrderShoesDTO toDTO(OrderShoes orderShoes);

    OrderShoes toEntity(OrderShoesDTO orderShoesDTO);
}

