package com.stepx.stepx.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.model.OrderItem;

@Mapper(componentModel =  "spring") 
public interface OrderItemMapper {
    
    @Mapping(source = "orderShoes.id", target = "orderId")
    @Mapping(source = "shoe.id", target = "shoeId")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(source = "orderId", target = "orderShoes.id")
    @Mapping(source = "shoeId", target = "shoe.id")
    OrderItem toDomain(OrderItemDTO dto);

    List<OrderItemDTO> toDTOs(Collection<OrderItem> orderItems);
    
}
