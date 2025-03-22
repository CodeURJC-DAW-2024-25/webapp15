package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.model.Shoe;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel =  "spring", uses = {ShoeSizeStockMapper.class, ReviewMapper.class})
public interface ShoeMapper {

    @Mappings({
        @Mapping(target = "imageUrl1", expression = "java(\"/shop/\" + shoe.getId() + \"/image/1\")"),
        @Mapping(target = "imageUrl2", expression = "java(\"/shop/\" + shoe.getId() + \"/image/2\")"),
        @Mapping(target = "imageUrl3", expression = "java(\"/shop/\" + shoe.getId() + \"/image/3\")"),
        @Mapping(target = "shortDescription", source = "description")
    })
    ShoeDTO toDTO(Shoe shoe);

    @Mappings({
        @Mapping(target = "description", source = "shortDescription"),
        @Mapping(target = "image1", ignore = true),
        @Mapping(target = "image2", ignore = true),
        @Mapping(target = "image3", ignore = true),
        // sizeStocks y reviews ahora se mapean automáticamente si están bien definidos
    })
    Shoe toDomain(ShoeDTO shoeDTO);

}
