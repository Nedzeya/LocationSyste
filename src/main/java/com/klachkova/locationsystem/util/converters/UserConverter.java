package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converter for converting between User entities and UserDTO objects.
 * <p>
 * This component uses UserMapper to perform conversions between entities and DTOs.
 * </p>
 */
@Component
public class UserConverter {
    private final UserMapper userMapper;

    @Autowired
    public UserConverter() {
        this.userMapper = UserMapper.INSTANCE;
    }

    /**
     * Converts a UserDTO to a User entity.
     *
     * @param userDTO the UserDTO object to convert
     * @return the converted User entity
     */
    public User convertToEntity(UserDTO userDTO) {
        return userMapper.convertToEntity(userDTO);
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert
     * @return the converted UserDTO object
     */
    public UserDTO convertToDto(User user) {
        return userMapper.convertToDto(user);
    }
}