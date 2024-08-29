package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.AccessLevel;
import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.LocationAccess;
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

@Service
@Transactional(readOnly = true)
public class LocationAccessService {

    private final LocationAccessRepository locationAccessRepository;
    private final UserService userService;
    private final LocationRepository locationRepository;
    private final Validator validator;

    @Autowired
    public LocationAccessService(LocationAccessRepository locationAccessRepository,
                                 UserService userService,
                                 LocationRepository locationRepository,
                                 Validator validator) {
        this.locationAccessRepository = locationAccessRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.validator = validator;
    }

    public List<Location> getAllSharedLocations(User user) {
        List<LocationAccess> locationAccesses = locationAccessRepository.findByUser(user);
        return locationAccesses.stream()
                .map(LocationAccess::getLocation)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void shareLocation(int locationId, String userEmail, AccessLevel accessLevel) {
        User user = userService.findByEmail(userEmail);
        Location location = locationRepository.findById(locationId)
                .orElseThrow( () -> new NotFoundException("Location with ID " + locationId + " not found"));
        LocationAccess locationAccessToSave = new LocationAccess(user, location, accessLevel);
                validateLocationAccess(locationAccessToSave);
        locationAccessRepository.save(locationAccessToSave);
    }

    @Transactional
    public void updateLocationAccessByAccessLevel(int locationId, String userEmail, AccessLevel accessLevel) {
        User user = userService.findByEmail(userEmail);
        Location location = locationRepository.findById(locationId)
                .orElseThrow( () -> new NotFoundException("Location with ID " + locationId + " not found"));;
        LocationAccess locationAccess = locationAccessRepository.findByLocationAndUser(location, user)
                .orElseThrow(() -> new NotFoundException("LocationAccess with locationID " + locationId + " and userEmail " + userEmail + " not found"));

        locationAccess.setAccessLevel(accessLevel);

        validateLocationAccess(locationAccess);

        locationAccessRepository.save(locationAccess);
    }
    public List<User> getFriends(int locationId) {
        List<LocationAccess> accesses = locationAccessRepository.findByLocationId(locationId);
        return accesses.stream()
                .map(LocationAccess::getUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFriendToLocation(int userId, String friendEmail, String locationAddress, AccessLevel accessLevel) {
        User friendUser = userService.findByEmail(friendEmail);
        Location location = locationRepository.findByAddress(locationAddress)
                .orElseThrow(() -> new NotFoundException("Location with address " + locationAddress + " not found"));

        LocationAccess adminAccess = locationAccessRepository.findByLocationAndUser(location, userService.findById(userId))
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
