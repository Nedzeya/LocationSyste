package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.*;
import com.klachkova.locationsystem.repositories.LocationRepository;
import com.klachkova.locationsystem.repositories.UserRepository;
import com.klachkova.locationsystem.util.NotCreatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationAccessService locationAccessService;
    private final UserRepository userRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository,
                           LocationAccessService locationAccessService,
                           UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.locationAccessService = locationAccessService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerLocation(Location location) {
        if (!userRepository.existsByEmail(location.getOwner().getEmail())) {
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

    public List<List<Location>> getAvailableLocations(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
        List<Location> allOwnLocations = locationRepository.findAllByOwner(user);
        List<Location> allSharedLocations = locationAccessService.getAllSharedLocations(user);
        return Arrays.asList(allOwnLocations, allSharedLocations);
    }

    public List<User> getFriendsToLocation(int locationId) {

        return locationAccessService.getFriends(locationId);
    }


}
