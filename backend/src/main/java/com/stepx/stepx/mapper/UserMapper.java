package com.stepx.stepx.mapper;

import com.stepx.stepx.dto.UserDTO;

import com.stepx.stepx.model.User;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring")
public interface UserMapper{

    UserDTO toDTO(User user);
    User toDomain(UserDTO userDTO);
     List<UserDTO> toDTOs(Collection<User> users);
}