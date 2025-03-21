package com.stepx.stepx.mapper;

import java.util.Collection;
import java.util.List;

import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.model.ShoeSizeStock;

public interface ShoeSizeStockMapper {
    ShoeSizeStockDTO toDTO(ShoeSizeStock shoeSizeStock);
    ShoeSizeStock toDomain(ShoeSizeStockDTO shoeSizeStockDTO);
    List<ShoeSizeStockDTO> toDTOs(Collection<ShoeSizeStock> shoeSizeStocks);
    
}
