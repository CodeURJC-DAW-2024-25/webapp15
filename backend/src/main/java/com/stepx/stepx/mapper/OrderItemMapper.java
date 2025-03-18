package com.stepx.stepx.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.model.OrderItem;

@Mapper(componentModel =  "spring") 
public interface OrderItemMapper {
    OrderItemDTO toDTO(OrderItem orderItem);
    OrderItem toDomain(OrderItemDTO orderItemDTO);
    List<OrderItemDTO> toDTOs(Collection<OrderItem> orderItems);
    
}
