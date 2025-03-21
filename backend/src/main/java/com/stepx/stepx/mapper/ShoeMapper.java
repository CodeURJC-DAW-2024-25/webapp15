package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.model.Shoe;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring", uses = ShoeSizeStockMapper.class)
public interface ShoeMapper {

    ShoeDTO toDTO(Shoe shoe);
    Shoe toDoamin(ShoeDTO shoeDTO);

    
}
