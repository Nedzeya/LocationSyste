package com.klachkova.locationsystem.controllers;

import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.services.*;
import com.klachkova.locationsystem.util.annotations.USAddress;
import com.klachkova.locationsystem.util.annotations.ValidAccessLevel;
import com.klachkova.locationsystem.util.converters.LocationConverter;
import com.klachkova.locationsystem.util.converters.UserConverter;
import com.klachkova.locationsystem.util.validators.LocationValidator;
import com.klachkova.locationsystem.util.validators.UserValidator;
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
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final UserConverter userConverter;
    private final LocationService locationService;
    private final LocationConverter locationConverter;
    private final LocationValidator locationValidator;
    private final LocationAccessService locationAccessService;

    @Autowired
    public UserController(UserService userService,
                          UserValidator userValidator,
                          UserConverter userConverter, LocationService locationService, LocationConverter locationConverter, LocationValidator locationValidator, LocationAccessService locationAccessService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.userConverter = userConverter;
        this.locationService = locationService;
        this.locationConverter = locationConverter;
        this.locationValidator = locationValidator;
        this.locationAccessService = locationAccessService;
    }


    @PostMapping()
    public ResponseEntity<User> registerUser(@Validated @RequestBody UserDTO userDTO,
                                             BindingResult bindingResult) {

        User registeredUser = userConverter.convertToEntity(userDTO);
        userValidator.validate(registeredUser, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }

        userService.registerUser(registeredUser);

        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);

    }

    @GetMapping("/{id}/availableLocations") //own+shared with user
    public ResponseEntity<List<LocationDTO>> getAvailableLocations(@PathVariable int id) {


        List<Location> sharedLocations = locationService.getAllSharedLocations(userService.findById(id));

        List<LocationDTO> locationDTOs = sharedLocations.stream()
                .map(locationConverter::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(locationDTOs, HttpStatus.OK);
    }


    @PatchMapping("/{id}/addFriendToLocation")
    public ResponseEntity<String> addFriendToLocation(@PathVariable("id") int id,
                                                      @RequestParam("locationAddress") @USAddress String locationAddress,
                                                      @RequestParam("friendEmail") @Email String friendEmail,
                                                      @RequestParam("accessLevel") @ValidAccessLevel AccessLevel accessLevel,
                                                      BindingResult bindingResult) {
        User user = userService.findById(id);
        Location location = locationService.findByAddress(locationAddress);
        locationValidator.validate(location, bindingResult);
        User friendUser = userService.findByEmail(friendEmail);
        userValidator.validate(friendUser, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }

        locationAccessService.addFriendToLocation(location, user, friendUser, accessLevel);
        return ResponseEntity.ok("Friend added successfully.");

    }

}
