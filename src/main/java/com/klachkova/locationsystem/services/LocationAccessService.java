package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.AccessLevel;
import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.LocationAccess;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.LocationAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LocationAccessService {

    private final LocationAccessRepository locationAccessRepository;
    private final UserService userService;
    private final LocationService locationService;

    @Autowired
    public LocationAccessService(LocationAccessRepository locationAccessRepository,
                                 UserService userService,
                                 LocationService locationService) {
        this.locationAccessRepository = locationAccessRepository;
        this.userService = userService;
        this.locationService = locationService;
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
        locationAccessRepository.save(new LocationAccess(user, locationService.findById(locationId), accessLevel));
    }

    @Transactional
    public void updateLocationAccessByAccessLevel(int locationId, String userEmail, AccessLevel accessLevel) {
        User user = userService.findByEmail(userEmail);
        LocationAccess locationAccess = locationAccessRepository.findByLocationAndUser(locationService.findById(locationId), user)
                .orElseThrow(() -> new NoSuchElementException("LocationAccess with locationID" + locationId + " and userEmail " + userEmail + " not found"));
        locationAccess.setAccessLevel(accessLevel);
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
        Location location = locationService.findByAddress(locationAddress);
        LocationAccess adminAccess = locationAccessRepository.findByLocationAndUser(location,userService.findById(userId))
                .orElseThrow(() -> new SecurityException(
                        "User with ID " + userId + " does not have access to this location"));
        if (adminAccess.getAccessLevel() != AccessLevel.ADMIN) {
            throw new SecurityException(
                    "User with ID " + userId + " does not have ADMIN access to location with address " + locationAddress);
        }
        locationAccessRepository.save(new LocationAccess(friendUser, location, accessLevel));
    }
}
