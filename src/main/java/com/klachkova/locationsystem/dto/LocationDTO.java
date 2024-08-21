package com.klachkova.locationsystem.dto;

import com.klachkova.locationsystem.util.annotations.USAddress;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LocationDTO {
    @Column(name = "name")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 3, max = 30,
            message = "Name should be between 3 and 30 characters")
    private String name;

    @NotBlank(message = "Address should not be empty")
    @USAddress(message = "Address must be in US format (ex.: '123 Main St, Springfield, IL, 62704')")
    private String address;

    @NotBlank(message = "The field must not be empty")
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
