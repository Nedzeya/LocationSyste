package com.klachkova.locationsystem.controllers;

import com.klachkova.locationsystem.dto.*;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.services.*;
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
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationConverter locationConverter;
    private final LocationAccessService locationAccessService;
    private final UserService userService;
    private final UserConverter userConverter;


    @Autowired
    public LocationController(LocationService locationService,
                              LocationConverter locationConverter,
                              LocationAccessService locationAccessService,
                              UserService userService,
                              UserConverter userConverter) {
        this.locationService = locationService;
        this.locationConverter = locationConverter;
        this.locationAccessService = locationAccessService;
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @PostMapping()
    public ResponseEntity<LocationDTO> registerLocation(@Valid @RequestBody LocationDTO locationDTO) {

        Location registeredLocation = locationConverter.convertToEntity(locationDTO);
        locationService.registerLocation(registeredLocation);
        return new ResponseEntity<>(locationConverter.convertToDto(registeredLocation), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<String> shareLocation(@PathVariable("id") int id,
                                                @RequestParam("userEmail") @Email String userEmail,
                                                @RequestParam("accessLevel") @ValidAccessLevel AccessLevel accessLevel) {

        locationAccessService.shareLocation(id, userEmail, accessLevel);
        return ResponseEntity.ok("Location shared successfully");
    }


    @PatchMapping("/{id}/access")
    public ResponseEntity<String> updateAccessLevel(@PathVariable("id") int locationId,
                                                    @RequestParam("userEmail") @Email String userEmail,
                                                    @RequestParam("accessLevel") @ValidAccessLevel AccessLevel accessLevel) {
        locationAccessService.updateLocationAccessByAccessLevel(locationId, userEmail, accessLevel);
        return ResponseEntity.ok("Access level updated successfully.");
    }

    @GetMapping("{id}/friends") //all friend users on the location
    public ResponseEntity<List<UserDTO>> getAllFriendUsers (@PathVariable("id") int id) {
        List<UserDTO> friends = locationService.getFriendsToLocation(id)
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
        if (friends.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }
}

