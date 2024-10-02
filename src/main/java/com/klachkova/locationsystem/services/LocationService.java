package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.dto.*;
import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.repositories.LocationRepository;
import com.klachkova.locationsystem.repositories.UserRepository;
import com.klachkova.locationsystem.util.converters.*;
import com.klachkova.locationsystem.util.exceptions.NotCreatedException;
import com.klachkova.locationsystem.util.exceptions.NotFoundException;
import com.klachkova.locationsystem.util.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service class for managing Location entities.
 * <p>
 * Provides methods to register a new location, validate location data, find locations by address or ID, and retrieve available locations for a user.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class LocationService {

 //   private static final long CACHE_EXPIRATION = 10;

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;
    private final LocationAccessService locationAccessService;
    private final Validator validator;
  //  private final RedisTemplate<String, Object> redisTemplate;

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
     //   this.redisTemplate = redisTemplate;
    }

    /**
     * Registers a new location.
     * <p>
     * Converts the provided LocationDTO to a Location entity, validates it, and saves it to the repository.
     * Associates the location with the user specified in the DTO.
     * Caches the registered location for future retrieval.
     * </p>
     *
     * @param locationDTO the data transfer object representing the location to register
     * @return the registered location as a LocationDTO
     * @throws NotCreatedException if the location already exists or the user does not exist
     * @throws ValidationException if the location data is invalid
     */
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

    /**
     * Validates the provided Location entity.
     * <p>
     * Checks if the Location entity adheres to the validation constraints and throws an exception if any violations are found.
     * </p>
     *
     * @param location the Location entity to validate
     * @throws ValidationException if validation constraints are violated
     */
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

    /**
     * Retrieves a location by its unique address.
     * <p>
     * Throws an exception if no location with the given address is found.
     * </p>
     *
     * @param address the address of the location to retrieve
     * @return the Location entity with the given address
     * @throws NotFoundException if no location with the given address is found
     */
    public Location findByAddress(String address) {
        return locationRepository.findByAddress(address)
                .orElseThrow(() -> new NotFoundException("Location with address " + address + " not found"));
    }

    /**
     * Retrieves a Location by its ID.
     * First checks the Redis cache for the location.
     * If the location is not found in the cache, it retrieves
     * the location from the database and caches it for future access.
     *
     * @param id the ID of the location to retrieve
     * @return the Location entity with the given ID
     * @throws NotFoundException if no location with the given ID is found
     */
    public Location findById(int id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location with ID " + id + " not found"));
    }

    /**
     * Checks if a location with the given address exists.
     *
     * @param address the address to check
     * @return true if a location with the given address exists, otherwise false
     */
    public boolean existsByAddress(String address) {
        return locationRepository.existsByAddress(address);
    }

    /**
     * Retrieves available locations for a user.
     * <p>
     * This includes locations owned by the user and locations shared with the user.
     * </p>
     *
     * @param userId the ID of the user for whom to retrieve available locations
     * @return a list of lists containing DTOs for the user's owned and shared locations
     * @throws NotFoundException if no user with the given ID is found
     */
    public List<List<LocationDTO>> getAvailableLocations(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        String redisKey = "AvailableLocationsDTO" + userId;

        List<Location> allOwnLocations = locationRepository.findAllByOwner(user);
        List<Location> allSharedLocations = locationAccessService.getAllSharedLocations(user);
        List<List<Location>> availableLocations = Arrays.asList(allOwnLocations, allSharedLocations);
        return availableLocations.stream()
                .map(locationList -> locationList.stream()
                        .map(locationConverter::convertToDto)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all friends of a user for a specific location.
     *
     * @param locationId the ID of the location
     * @return a list of UserDTOs representing friends with access to the location
     */
    public List<UserDTO> getFriendsToLocation(int locationId) {
        return locationAccessService.getFriends(locationId)
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
    }
}
