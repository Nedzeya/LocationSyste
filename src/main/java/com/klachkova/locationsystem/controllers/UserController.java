package com.klachkova.locationsystem.controllers;

import com.klachkova.locationsystem.dto.*;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.services.*;
import com.klachkova.locationsystem.util.exceptions.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users in the location system.
 * <p>
 * Provides endpoints for user registration, fetching available locations, and managing user access to locations.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final LocationService locationService;
    private final LocationAccessService locationAccessService;

    @Autowired
    public UserController(
        UserService userService,
        LocationService locationService,
        LocationAccessService locationAccessService
    ) {

        this.userService = userService;
        this.locationService = locationService;
        this.locationAccessService = locationAccessService;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO the user data transfer object containing user details
     * @return a ResponseEntity with the registered user data or an error message
     */

    @PostMapping()
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {

        try {
            UserDTO registeredUserDTO = userService.registerUser(userDTO);
            return new ResponseEntity<>(registeredUserDTO, HttpStatus.CREATED);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves available locations for a user (own+shared with user).
     * <p>
     * This includes locations owned by the user and locations shared with the user.
     * </p>
     *
     * @param id the user ID
     * @return a ResponseEntity with a list of available locations or an error message
     */

    @GetMapping("/{id}/availableLocations")
    public ResponseEntity<?> getAvailableLocations(@PathVariable int id) {

        try {
            List<List<LocationDTO>> availableLocations = locationService.getAvailableLocations(id);
            return new ResponseEntity<>(availableLocations, HttpStatus.OK);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a friend to a location with a specified access level.
     *
     * @param userId          the ID of the user adding the friend
     * @param friendEmail     the email of the friend to be added
     * @param locationAddress the address of the location to which the friend is being added
     * @param accessLevel     the access level to be granted to the friend
     * @return a ResponseEntity with a success message or an error message
     */
    @PatchMapping("/{id}/addFriendToLocation")
    public ResponseEntity<String> addFriendToLocation(
        @PathVariable("id") int userId,
        @RequestParam("friendEmail") String friendEmail,
        @RequestParam("locationAddress") String locationAddress,
        @RequestParam("accessLevel") AccessLevel accessLevel
    ) {

        try {
            locationAccessService.addFriendToLocation(userId, friendEmail, locationAddress, accessLevel);
            return ResponseEntity.ok("Friend added successfully.");
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
