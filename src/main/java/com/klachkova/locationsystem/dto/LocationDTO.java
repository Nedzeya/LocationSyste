package com.klachkova.locationsystem.dto;
/**
 * Data Transfer Object (DTO) for {@link Location}.
 * <p>
 * This class is used to transfer location data between different layers of the application,
 * such as between the API layer and the service layer. It includes only the relevant fields
 * needed for representation and hides internal details of the {@link Location} entity.
 * </p>
 */
public class LocationDTO {
    /**
     * The name of the location.
     * <p>
     * This field contains the name of the location and is used in API requests and responses.
     * </p>
     */
    private String name;
    /**
     * The address of the location.
     * <p>
     * This field contains the address of the location and is used in API requests and responses.
     * </p>
     */
    private String address;
    /**
     * The owner of the location.
     * <p>
     * This field contains the {@link UserDTO} representing the owner of the location.
     * It is used in API requests and responses to provide information about the location's owner.
     * </p>
     */
    private UserDTO owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }
}
