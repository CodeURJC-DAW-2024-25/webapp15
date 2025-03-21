package com.stepx.stepx.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.model.ShoeSizeStock;

@Mapper(componentModel =  "spring",uses = ShoeMapper.class) 
public interface ShoeSizeStockMapper {

    @Mapping(source = "shoe.id", target = "shoeId")  // Extrae solo el ID de Shoe
    ShoeSizeStockDTO toDTO(ShoeSizeStock shoeSizeStock);
    //@Mapping(source = "shoeId", target = "shoe") // Usa ShoeMapper para convertir el ID en Shoe
    ShoeSizeStock toDomain(ShoeSizeStockDTO shoeSizeStockDTO); 
    
    List<ShoeSizeStock> toDomains(List<ShoeSizeStockDTO> shoeSizeStockDTOs);
    List<ShoeSizeStockDTO> toDTOs(Collection<ShoeSizeStock> shoeSizeStocks);
    
}
