package com.klachkova.locationsystem.dto;
/**
 * Data Transfer Object (DTO) for {@link User}.
 * <p>
 * This class is used to transfer user data between different layers of the application,
 * particularly from the API layer to the service layer, and vice versa. DTOs are designed
 * to encapsulate only the relevant fields that should be exposed or processed by the API,
 * thereby hiding internal details of the underlying entity, such as the ID.
 * </p>
 */
public class UserDTO {
    /**
     * The name of the user.
     * <p>
     * This field contains the user's name. It is included in the DTO for use in API requests and responses.
     * </p>
     */
    private String name;
    /**
     * The email address of the user.
     * <p>
     * This field contains the user's email address. It is included in the DTO for use in API requests and responses.
     * </p>
     */
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
