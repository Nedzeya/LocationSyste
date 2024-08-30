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
 * REST controller for managing locations in the location system.
 * <p>
 * Provides endpoints for registering locations, sharing locations with users,
 * updating access levels, and retrieving all friends associated with a location.
 * </p>
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationAccessService locationAccessService;

    @Autowired
    public LocationController(LocationService locationService,
                              LocationAccessService locationAccessService) {
        this.locationService = locationService;
        this.locationAccessService = locationAccessService;
    }

    /**
     * Registers a new location.
     *
     * @param locationDTO the location data transfer object containing location details
     * @return a ResponseEntity with the registered location data or an error message
     */
    @PostMapping()
    public ResponseEntity<?> registerLocation(@RequestBody LocationDTO locationDTO) {
        try {
            LocationDTO registeredLocationDTO = locationService.registerLocation(locationDTO);
            return new ResponseEntity<>(registeredLocationDTO, HttpStatus.CREATED);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shares a location with a user, granting the specified access level.
     *
     * @param id          the ID of the location to be shared
     * @param userEmail   the email of the user with whom the location is to be shared
     * @param accessLevel the access level to be granted to the user
     * @return a ResponseEntity with a success message or an error message
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<?> shareLocation(@PathVariable("id") int id,
                                           @RequestParam("userEmail") String userEmail,
                                           @RequestParam("accessLevel") AccessLevel accessLevel) {
        try {
            System.out.println("locationController sharing " + id + userEmail + accessLevel);
            locationAccessService.shareLocation(id, userEmail, accessLevel);
            return new ResponseEntity<>("Location shared successfully", HttpStatus.OK);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the access level for a user for a specific location.
     *
     * @param locationId  the ID of the location
     * @param userEmail   the email of the user whose access level is to be updated
     * @param accessLevel the new access level to be granted
     * @return a ResponseEntity with a success message or an error message
     */
    @PatchMapping("/{id}/access")
    public ResponseEntity<String> updateAccessLevel(@PathVariable("id") int locationId,
                                                    @RequestParam("userEmail") String userEmail,
                                                    @RequestParam("accessLevel") AccessLevel accessLevel
    ) {
        try {
            locationAccessService.updateLocationAccessByAccessLevel(locationId, userEmail, accessLevel);
            return new ResponseEntity<>("Access level updated successfully.", HttpStatus.OK);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all users who have access to a specific location.
     *
     * @param id the ID of the location
     * @return a ResponseEntity with a list of users or a no content status if no users are found
     */
    @GetMapping("{id}/friends") //all friend users on the location
    public ResponseEntity<?> getAllFriendUsers(@PathVariable("id") int id) {
        try {
            List<UserDTO> friends = locationService.getFriendsToLocation(id);
            if (friends.isEmpty()) {
                return new ResponseEntity<>(friends, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } catch (ApplicationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

