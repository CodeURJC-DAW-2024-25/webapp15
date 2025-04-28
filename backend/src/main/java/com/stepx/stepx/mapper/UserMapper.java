package com.stepx.stepx.mapper;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import com.stepx.stepx.dto.UserDTO;

import com.stepx.stepx.model.User;

import java.util.Collection;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
        //@Mapping(source = "firstname", target = "firstname") ,// Agregar este mapeo en toDTO
        @Mapping(source = "encodedPassword", target = "password"),
        @Mapping(target = "imageString", expression = "java(convertBlobToBase64(user.getImageUser()))") // Blob → Base64
    })
    UserDTO toDTO(User user);

    @Mappings({   
        @Mapping(target = "imageUser", ignore = true),
        @Mapping(source = "password", target = "encodedPassword") // Agregar este mapeo en toDomain
    })
    User toDomain(UserDTO userDTO);

    // Métodos para conversión Blob ↔ Base64
    default String convertBlobToBase64(Blob blob) {
        if (blob == null) return null;
        try {
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (SQLException e) {
            throw new RuntimeException("Error converting Blob to Base64", e);
        }
    }

    default Blob convertBase64ToBlob(String base64) {
        if (base64 == null) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            return new SerialBlob(bytes);
        } catch (SQLException e) {
            throw new RuntimeException("Error converting Base64 to Blob", e);
        }
    }
}