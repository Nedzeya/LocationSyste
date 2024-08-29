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

