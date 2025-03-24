package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.UserDTO;

import com.stepx.stepx.model.User;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel =  "spring")
public interface UserMapper {

    @Mappings({
        
        @Mapping(source = "encodedPassword", target = "password") // Agregar este mapeo en toDTO

    })
    UserDTO toDTO(User user);

    @Mappings({   
        @Mapping(target = "imageUser", ignore = true),
        @Mapping(source = "password", target = "encodedPassword") // Agregar este mapeo en toDomain
    })
    User toDomain(UserDTO userDTO);

    List<UserDTO> toDTOs(Collection<User> users);
}
