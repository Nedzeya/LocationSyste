package com.klachkova.locationsystem.dto;

import javax.validation.constraints.*;

public class UserDTO {
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 3, max = 30,
            message = "Name should be between 3 and 30 characters")
    private String name;

    @Email (message = "Email should be valid")
    @NotEmpty (message = "Email should not be empty")
    private String email;
}
