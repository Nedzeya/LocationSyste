package com.klachkova.locationsystem.controllers;


import com.klachkova.locationsystem.dto.*;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.services.LocationAccessService;
import com.klachkova.locationsystem.services.LocationService;
import com.klachkova.locationsystem.services.UserService;
import com.klachkova.locationsystem.util.annotations.ValidAccessLevel;
import com.klachkova.locationsystem.util.converters.*;
import com.klachkova.locationsystem.util.validators.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.stream.Collectors;

import static com.klachkova.locationsystem.util.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationValidator locationValidator;
    private final LocationConverter locationConverter;
    private final LocationAccessService locationAccessService;
    private final LocationAccessConverter locationAccessConverter;
    private final LocationAccessValidator locationAccessValidator;
    private final UserService userService;
    private final UserValidator userValidator;
    private final UserConverter userConverter;


    @Autowired
    public LocationController(LocationService locationService,
                              LocationValidator locationValidator,
                              LocationConverter locationConverter,
                              LocationAccessService locationAccessService, LocationAccessConverter locationAccessConverter,
                              LocationAccessValidator locationAccessValidator,
                              UserService userService,
                              UserValidator userValidator, UserConverter userConverter) {
        this.locationService = locationService;
        this.locationValidator = locationValidator;
        this.locationConverter = locationConverter;
        this.locationAccessService = locationAccessService;
        this.locationAccessConverter = locationAccessConverter;
        this.locationAccessValidator = locationAccessValidator;
        this.userService = userService;
        this.userValidator = userValidator;
        this.userConverter = userConverter;
    }


    @PostMapping()
    public ResponseEntity<Location> registerLocation(@Validated @RequestBody LocationDTO locationDTO,
                                                     BindingResult bindingResult) {

        Location registeredLocation = locationConverter.convertToEntity(locationDTO);
        locationValidator.validate(registeredLocation, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }

        locationService.registerLocation(registeredLocation);

        return new ResponseEntity<>(registeredLocation, HttpStatus.CREATED);

    }

    // for testing
    @GetMapping()
    public List<LocationDTO> getAllLocations() {
        return locationService.findAll()
                .stream()
                .map(locationConverter::convertToDto)
                .collect(Collectors.toList());

    }

    @GetMapping("/{id}")
    public LocationDTO getLocation(@PathVariable("id") int id) {
        return locationConverter.convertToDto(locationService.findById(id));
    }

    //
    @PostMapping("/{id}/share")
    public ResponseEntity<String> shareLocation(@PathVariable("id") int id,
                                                @Validated @RequestBody LocationAccessDTO locationAccessDTO,
                                                BindingResult bindingResult) {

        LocationAccess newLocationAccess = locationAccessConverter.convertToEntity(locationAccessDTO);
        locationAccessValidator.validate(newLocationAccess, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }
        locationService.shareLocation(id, newLocationAccess);
        return ResponseEntity.ok("Location shared successfully");

    }


    @PatchMapping("/{id}/access")
    public ResponseEntity<String> updateAccessLevel(@PathVariable("id") int locationId,
                                                    @RequestParam("userEmail") @Email String userEmail,
                                                    @RequestParam("accessLevel") @ValidAccessLevel AccessLevel accessLevel,
                                                    BindingResult bindingResult) {

        User user = userService.findByEmail(userEmail);

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }

        locationAccessService.updateLocationAccessByAccessLevel(locationId, user, accessLevel);

        return ResponseEntity.ok("Access level updated successfully.");

    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<UserDTO>> getAllUsersWithAccess(@PathVariable("id") int id) {
        List<UserDTO> usersWithAccess = locationService.getFriendsWithAccessToLocation(id)
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());

        if (usersWithAccess.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usersWithAccess, HttpStatus.OK);
    }


}

