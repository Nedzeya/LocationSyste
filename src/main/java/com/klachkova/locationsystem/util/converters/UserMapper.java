package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "id", ignore = true)
    User convertToEntity(UserDTO userDTO);

    UserDTO convertToDto(User user);
}