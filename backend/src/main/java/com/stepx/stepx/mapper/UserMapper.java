package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.UserDTO;

import com.stepx.stepx.model.User;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel =  "spring")
public interface UserMapper{


     @Mappings({        
        @Mapping(target = "imageUser", expression = "java(\"/user/\" + user.getId() + \"/image/\")"),
    })

    UserDTO toDTO(User user);

    @Mappings({   
        @Mapping(target = "imageUser", ignore = true),
    })

     User toDomain(UserDTO userDTO);

     List<UserDTO> toDTOs(Collection<User> users);
}