package com.klachkova.locationsystem.dto;

import javax.validation.constraints.*;

public class UserDTO {
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name;

    @Email (message = "Email should be valid")
    @NotBlank (message = "Email should not be empty")
    private String email;
}
