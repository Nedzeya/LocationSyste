package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.repositories.LocationRepository;
import com.klachkova.locationsystem.util.NotCreatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationAccessService locationAccessService;
    private final UserService userService;

    @Autowired
    public LocationService(LocationRepository locationRepository,
                           LocationAccessService locationAccessService,
                           UserService userService) {
        this.locationRepository = locationRepository;
        this.locationAccessService = locationAccessService;
        this.userService = userService;
    }

    @Transactional
    public void registerLocation(Location location) {
        if (!userService.existsByEmail(location.getOwner().getEmail())) {
            throw new NotCreatedException("No such user in the database");
        }
        if (existsByAddress(location.getAddress())) {
            throw new NotCreatedException("Location with that address already exists");
        }
        locationRepository.save(location);
    }

    public Location findByAddress(String address) {
        return locationRepository.findByAddress(address)
                .orElseThrow(() -> new NoSuchElementException("Location with address " + address + " not found"));
    }

    public Location findById(int id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Location with ID " + id + " not found"));
    }

    public boolean existsByAddress(String address) {
        return locationRepository.existsByAddress(address);
    }

    public List<Location> getAllSharedLocations(User user) {
        return locationAccessService.getAllSharedLocations(user);
    }

    public List<User> getFriendsToLocation(int locationId) {
        return locationAccessService.getFriends (locationId);
    }
}
