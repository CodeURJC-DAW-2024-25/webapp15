package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.model.OrderShoes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, UserMapper.class})
public interface OrderShoesMapper {
    
    @Mapping(target = "userId", source = "user.id")
    OrderShoesDTO toDTO(OrderShoes orderShoes);
    
    @Mapping(target = "user.id", source = "userId")
    OrderShoes toDomain(OrderShoesDTO orderShoesDTO);

}

