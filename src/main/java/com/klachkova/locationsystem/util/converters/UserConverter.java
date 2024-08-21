package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public UserConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToEntity(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }

    public UserDTO convertToDto(User user){
        return modelMapper.map(user, UserDTO.class);
    }
}
