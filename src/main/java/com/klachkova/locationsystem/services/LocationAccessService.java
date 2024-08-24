package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.AccessLevel;
import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.LocationAccess;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.LocationAccessRepository;
import com.klachkova.locationsystem.repositories.LocationRepository;
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
    private final LocationRepository locationRepository;

    @Autowired
    public LocationAccessService(LocationAccessRepository locationAccessRepository,
                                 LocationRepository locationRepository) {
        this.locationAccessRepository = locationAccessRepository;
        this.locationRepository = locationRepository;
    }


    public List<LocationAccess> findAll() {
        return locationAccessRepository.findAll();
    }

    public List<Location> getAllSharedLocations(User user) {
        List<LocationAccess> locationAccesses = locationAccessRepository.findByUser(user);

        return locationAccesses.stream()
                .map(LocationAccess::getLocation)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void shareLocation(int locationId, User user, AccessLevel accessLevel) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location with ID " + locationId + " not found"));

        LocationAccess locationAccess = new LocationAccess(user, location, accessLevel);

        locationAccessRepository.save(locationAccess);
    }

    @Transactional
    public void updateLocationAccessByAccessLevel(int locationId, User user, AccessLevel accessLevel) {
        LocationAccess locationAccess = locationAccessRepository.findByLocationIdAndUser(locationId, user)
                .orElseThrow(() -> new NoSuchElementException("LocationAccess with locationId" + locationId + " and user " + user + " not found"));
        locationAccess.setAccessLevel(accessLevel);
        locationAccessRepository.save(locationAccess);
    }

    public List<User> getFriendsWithAccess(int locationId) {

        List<LocationAccess> accesses = locationAccessRepository.findByLocationId(locationId);

        return accesses.stream()
                .map(LocationAccess::getUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFriendToLocation(Location location, User user, User friendUser, AccessLevel accessLevel) {

        LocationAccess adminAccess = locationAccessRepository.findByLocationIdAndUser(location.getId(), user)
                .orElseThrow(() -> new IllegalArgumentException("User does not have access to this location"));

        if (adminAccess.getAccessLevel() != AccessLevel.ADMIN) {
            throw new SecurityException("User does not have admin access to this location");
        }

        locationAccessRepository.save(new LocationAccess(friendUser, location, accessLevel));

    }


}
