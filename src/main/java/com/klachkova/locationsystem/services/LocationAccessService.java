package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.LocationAccessRepository;
import com.klachkova.locationsystem.repositories.LocationRepository;
import com.klachkova.locationsystem.util.exceptions.NotFoundException;
import com.klachkova.locationsystem.util.exceptions.PermissionDeniedException;
import com.klachkova.locationsystem.util.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Service class for managing location access and sharing.
 * <p>
 * Provides methods to share locations, update access levels, retrieve shared locations, and manage location access.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class LocationAccessService {

    private final LocationAccessRepository locationAccessRepository;
    private final UserService userService;
    private final LocationRepository locationRepository;
    private final Validator validator;

    @Autowired
    public LocationAccessService(
        LocationAccessRepository locationAccessRepository,
        UserService userService,
        LocationRepository locationRepository,
        Validator validator
    ) {

        this.locationAccessRepository = locationAccessRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.validator = validator;
    }

    /**
     * Retrieves all locations shared with a user.
     *
     * @param user the user whose shared locations are to be retrieved
     * @return a list of locations shared with the user
     */
    public List<Location> getAllSharedLocations(User user) {

        List<LocationAccess> locationAccesses = locationAccessRepository.findByUser(user);
        return locationAccesses.stream()
            .map(LocationAccess::getLocation)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Shares a location with a user.
     * <p>
     * Creates a new LocationAccess entry for the user and the location with the specified access level.
     * </p>
     *
     * @param locationId  the ID of the location to be shared
     * @param userEmail   the email of the user to share the location with
     * @param accessLevel the access level to grant
     * @throws NotFoundException   if the location or user is not found
     * @throws ValidationException if the LocationAccess data is invalid
     */
    @Transactional
    public void shareLocation(int locationId, String userEmail, AccessLevel accessLevel) {

        User user = userService.findByEmail(userEmail);
        Location location = locationRepository.findById(locationId)
            .orElseThrow(() -> new NotFoundException("Location with ID " + locationId + " not found"));
        LocationAccess locationAccessToSave = new LocationAccess(user, location, accessLevel);
        validateLocationAccess(locationAccessToSave);
        locationAccessRepository.save(locationAccessToSave);
    }

    /**
     * Updates the access level for a user at a specific location.
     * <p>
     * Finds the LocationAccess entry and updates the access level.
     * </p>
     *
     * @param locationId  the ID of the location
     * @param userEmail   the email of the user whose access level is to be updated
     * @param accessLevel the new access level
     * @throws NotFoundException   if the location or location access entry is not found
     * @throws ValidationException if the LocationAccess data is invalid
     */
    @Transactional
    public void updateLocationAccessByAccessLevel(int locationId, String userEmail, AccessLevel accessLevel) {

        User user = userService.findByEmail(userEmail);
        Location location = locationRepository.findById(locationId)
            .orElseThrow(() -> new NotFoundException("Location with ID " + locationId + " not found"));
        ;
        LocationAccess locationAccess = locationAccessRepository.findByLocationAndUser(location, user)
            .orElseThrow(() -> new NotFoundException("LocationAccess with locationID " + locationId + " and userEmail" +
                " " + userEmail + " not found"));

        locationAccess.setAccessLevel(accessLevel);

        validateLocationAccess(locationAccess);

        locationAccessRepository.save(locationAccess);
    }

    /**
     * Retrieves all users with access to a specific location.
     *
     * @param locationId the ID of the location
     * @return a list of users with access to the location
     */
    public List<User> getFriends(int locationId) {

        List<LocationAccess> accesses = locationAccessRepository.findByLocationId(locationId);
        return accesses.stream()
            .map(LocationAccess::getUser)
            .collect(Collectors.toList());
    }

    /**
     * Adds a friend to a location.
     * <p>
     * Checks if the requesting user has ADMIN access to the location before adding the friend.
     * </p>
     *
     * @param userId          the ID of the user making the request
     * @param friendEmail     the email of the friend to be added
     * @param locationAddress the address of the location
     * @param accessLevel     the access level to grant to the friend
     * @throws NotFoundException         if the location or friend is not found, or if the user does not have access
     * to the location
     * @throws PermissionDeniedException if the user does not have ADMIN access to the location
     * @throws ValidationException       if the LocationAccess data is invalid
     */
    @Transactional
    public void addFriendToLocation(int userId, String friendEmail, String locationAddress, AccessLevel accessLevel) {

        User friendUser = userService.findByEmail(friendEmail);
        Location location = locationRepository.findByAddress(locationAddress)
            .orElseThrow(() -> new NotFoundException("Location with address " + locationAddress + " not found"));

        LocationAccess adminAccess = locationAccessRepository.findByLocationAndUser(location,
                userService.findById(userId))
            .orElseThrow(() -> new NotFoundException(
                "User with ID " + userId + " does not have access to this location"));
        if (adminAccess.getAccessLevel() != AccessLevel.ADMIN) {
            throw new PermissionDeniedException(
                "User with ID " + userId + " does not have ADMIN access to location with address " + locationAddress);
        }

        LocationAccess locationAccess = new LocationAccess(friendUser, location, accessLevel);

        validateLocationAccess(locationAccess);

        locationAccessRepository.save(locationAccess);
    }

    /**
     * Validates the LocationAccess entity.
     * <p>
     * Checks if the LocationAccess entity adheres to validation constraints and throws an exception if any
     * violations are found.
     * </p>
     *
     * @param locationAccess the LocationAccess entity to validate
     * @throws ValidationException if validation constraints are violated
     */
    public void validateLocationAccess(LocationAccess locationAccess) {

        Set<ConstraintViolation<LocationAccess>> violations = validator.validate(locationAccess);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<LocationAccess> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            throw new ValidationException(sb.toString().trim());
        }
    }
}
