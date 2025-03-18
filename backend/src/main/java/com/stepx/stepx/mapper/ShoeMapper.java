package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.model.Shoe;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring")
public interface ShoeMapper {
    ShoeMapper INSTANCE = Mappers.getMapper(ShoeMapper.class);

    ShoeDTO toDTO(Shoe shoe);
    Shoe toDoamin(ShoeDTO shoeDTO);
}
