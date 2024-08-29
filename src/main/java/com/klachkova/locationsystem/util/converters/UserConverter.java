package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserConverter {
    private final UserMapper userMapper;

    @Autowired
    public UserConverter() {
        this.userMapper = UserMapper.INSTANCE;
    }

    public User convertToEntity(UserDTO userDTO) {
        return userMapper.convertToEntity(userDTO);
    }

    public UserDTO convertToDto(User user) {
        return userMapper.convertToDto(user);
    }
}