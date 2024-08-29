package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.repositories.LocationRepository;
import com.klachkova.locationsystem.repositories.UserRepository;
import com.klachkova.locationsystem.util.converters.LocationConverter;
import com.klachkova.locationsystem.util.converters.UserConverter;
import com.klachkova.locationsystem.util.exceptions.NotCreatedException;
import com.klachkova.locationsystem.util.exceptions.NotFoundException;
import com.klachkova.locationsystem.util.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class LocationService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;
    private final LocationAccessService locationAccessService;
    private final Validator validator;

    @Autowired
    public LocationService(UserRepository userRepository,
                           UserConverter userConverter,
                           com.klachkova.locationsystem.repositories.LocationRepository locationRepository,
                           LocationConverter locationConverter,
                           LocationAccessService locationAccessService,
                           Validator validator) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.locationRepository = locationRepository;
        this.locationConverter = locationConverter;
        this.locationAccessService = locationAccessService;
        this.validator = validator;
    }

    @Transactional
    public LocationDTO registerLocation(LocationDTO locationDTO) {
        Location locationToRegister = locationConverter.convertToEntity(locationDTO);
        validateLocation(locationToRegister);
        User existingUser = userRepository.findByEmail(locationDTO.getOwner().getEmail())
                .orElseThrow(() -> new NotCreatedException("No such user in the database"));
        locationToRegister.setOwner(existingUser);
        if (existsByAddress(locationToRegister.getAddress())) {
            throw new NotCreatedException("Location with that address already exists");
        }
        Location registeredLocation = locationRepository.save(locationToRegister);
        return locationConverter.convertToDto(registeredLocation);
    }

    private void validateLocation(Location location) {
        Set<ConstraintViolation<Location>> violations = validator.validate(location);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Location> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            throw new ValidationException(sb.toString().trim());
        }
    }

    public Location findByAddress(String address) {
        return locationRepository.findByAddress(address)
                .orElseThrow(() -> new NotFoundException("Location with address " + address + " not found"));
    }

    public Location findById(int id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location with ID " + id + " not found"));
    }

    public boolean existsByAddress(String address) {
        return locationRepository.existsByAddress(address);
    }

    public List<List<LocationDTO>> getAvailableLocations(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<Location> allOwnLocations = locationRepository.findAllByOwner(user);
        List<Location> allSharedLocations = locationAccessService.getAllSharedLocations(user);
        List<List<Location>> availableLocations = Arrays.asList(allOwnLocations, allSharedLocations);
        return availableLocations.stream()
                .map(locationList -> locationList.stream()
                        .map(locationConverter::convertToDto)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFriendsToLocation(int locationId) {
        return locationAccessService.getFriends(locationId)
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
    }
}
