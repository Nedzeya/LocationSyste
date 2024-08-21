package com.klachkova.locationsystem.controllers;


import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.services.LocationService;
import com.klachkova.locationsystem.util.converters.LocationConverter;
import com.klachkova.locationsystem.util.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.klachkova.locationsystem.util.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationValidator locationValidator;
    private final LocationConverter locationConverter;

    @Autowired
    public LocationController(LocationService locationService,
                              LocationValidator locationValidator,
                              LocationConverter locationConverter) {
        this.locationService = locationService;
        this.locationValidator = locationValidator;
        this.locationConverter = locationConverter;
    }

    @GetMapping()
    public List<LocationDTO> getAllLocations() {
        return locationService.findAll()
                .stream()
                .map(locationConverter::convertToDto)
                .collect(Collectors.toList());

    }

    @GetMapping("byId/{id}")
    public LocationDTO getLocation(@PathVariable("id") int id) {
        return locationConverter.convertToDto(locationService.findOne(id));
    }

    @PostMapping("/register")
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

}
