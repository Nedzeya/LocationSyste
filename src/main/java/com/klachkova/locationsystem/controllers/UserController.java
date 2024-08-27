package com.klachkova.locationsystem.controllers;

import com.klachkova.locationsystem.dto.*;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.services.*;
import com.klachkova.locationsystem.util.annotations.USAddress;
import com.klachkova.locationsystem.util.annotations.ValidAccessLevel;
import com.klachkova.locationsystem.util.converters.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserConverter userConverter;
    private final LocationService locationService;
    private final LocationConverter locationConverter;
    private final LocationAccessService locationAccessService;

    @Autowired
    public UserController(UserService userService,
                          UserConverter userConverter,
                          LocationService locationService,
                          LocationConverter locationConverter,
                          LocationAccessService locationAccessService) {
        this.userService = userService;
        this.userConverter = userConverter;
        this.locationService = locationService;
        this.locationConverter = locationConverter;
        this.locationAccessService = locationAccessService;
    }

    @PostMapping()
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User userToRegister = userConverter.convertToEntity(userDTO);
        User registeredUser = userService.registerUser(userToRegister);
        UserDTO registeredUserDTO = userConverter.convertToDto(registeredUser);
        return new ResponseEntity<>(registeredUserDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/availableLocations") //own+shared with user
    public ResponseEntity<List<List<LocationDTO>>> getAvailableLocations(@PathVariable int id) {
        List<List<Location>> availableLocations = locationService.getAvailableLocations(id);
        List<List<LocationDTO>> locationDTOs = availableLocations.stream()
                .map(locationList -> locationList.stream()
                        .map(locationConverter::convertToDto)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(locationDTOs, HttpStatus.OK);
    }


    @PatchMapping("/{id}/addFriendToLocation")
    public ResponseEntity<String> addFriendToLocation(@PathVariable("id") int userId,
                                                      @RequestParam("friendEmail") @Email String friendEmail,
                                                      @RequestParam("locationAddress") @USAddress String locationAddress,
                                                      @RequestParam("accessLevel") @ValidAccessLevel AccessLevel accessLevel) {
        locationAccessService.addFriendToLocation(userId, friendEmail, locationAddress, accessLevel);
        return ResponseEntity.ok("Friend added successfully.");
    }
}
