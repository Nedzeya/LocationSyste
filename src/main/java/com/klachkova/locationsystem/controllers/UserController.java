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
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final LocationService locationService;
    private final LocationAccessService locationAccessService;

    @Autowired
    public UserController(UserService userService,
                          LocationService locationService,
                          LocationAccessService locationAccessService) {
        this.userService = userService;
        this.locationService = locationService;
        this.locationAccessService = locationAccessService;
    }

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

    @GetMapping("/{id}/availableLocations") //own+shared with user
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

    @PatchMapping("/{id}/addFriendToLocation")
    public ResponseEntity<String> addFriendToLocation(@PathVariable("id") int userId,
                                                      @RequestParam("friendEmail") String friendEmail,
                                                      @RequestParam("locationAddress") String locationAddress,
                                                      @RequestParam("accessLevel") AccessLevel accessLevel) {
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
