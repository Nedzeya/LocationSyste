package com.klachkova.locationsystem.dto;

import com.klachkova.locationsystem.modeles.AccessLevel;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

public class LocationAccessDTO {

    @NotBlank(message = "The field must not be empty")
    private UserDTO userDTO;

    @NotBlank(message = "The field must not be empty")
    private LocationDTO locationDTO;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;
}
